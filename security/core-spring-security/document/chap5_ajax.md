# Chap 5) Ajax 인증 구현

## :pushpin: 먼저 알아야할 점

- 디버깅으로 인증 관련 필터를 확인하고 싶으면 `AbstractAuthenticationProcessingFilter` 참고
- `UsernamePasswordAuthenticationFilter`은 `AbstractAuthenticationProcessingFilter`을 상속받은 클래스
- Custom한 인증 filter를 등록하기 위해서 `AbstractAuthenticationProcessingFilter` 이것을 상속받아서 등록하면 됨
- 근데 중요한 것은 custom filter에 따로 설정해야할 것들이 있다. 
  - `AuthenticationManager` 따로 등록해야 한다. (아래 AjaxAuthenticationProvider 주의점 내용 참고)
  - custom success/failure handler 등록할 때에도 해당 custom filter에 따로 등록해야 한다.
- `ajax.http`
  - `POST /api/login`하게 되면 로그인에 대한 인증 처리 진행 후 성공하면 SC 객체를 세션에 저장
  - 저장된 세션의 ID 값을 클라이언트에 cookie 저장하도록 응답(set-cookies)
  - `http-client.cookies` 파일에는 쿠키에 저장된 내용 저장(해당 세션 ID 값, `JSESSIONID`)

<br>

## :pushpin: 인증 필터 - AjaxAuthenticationFilter
- AbstractAuthenticationFilter를 상속받아서 config에 등록해야함
- 여기서 문제가 Spring Security `WebSecurityConfigurerAdapter` deprecated 문제
- AuthenticationManager를 따로 Bean 설정해서 Custom Filter에 주입해줘야 한다.

```kt
// Custom AbstractAuthenticationProcessingFilter 적용하기 위해서 
// AuthenticationManager 따로 등록해야한다.
// https://ttl-blog.tistory.com/269
@Bean
fun authenticationManager(): AuthenticationManager {
    val provider = DaoAuthenticationProvider()
    provider.setPasswordEncoder(passwordEncoder())
    return ProviderManager(provider)
}
```
- 위 방법은 Custom AuthenticationManager 등록하는 것인데 비추

```kt
@Bean
fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager {
    return authenticationConfiguration.authenticationManager
}
```
- 위와 같이 Manager 등록하는 것이 좋다.
- 그런데 위와 같은 방식말고 `AjaxLoginProcessingFilter`에 `authenticationManager`를 주입시켜주려면 위와 같은 방식도 제약이 존재

```kt
// 필드 주입
@Autowired
lateinit var authenticationConfiguration: AuthenticationConfiguration

@Bean
fun authenticationManager(): AuthenticationManager {
    return authenticationConfiguration.authenticationManager
}

@Bean
fun ajaxLoginProcessingFilter(): AjaxLoginProcessingFilter {
    return AjaxLoginProcessingFilter().apply {
        this.setAuthenticationManager(authenticationManager())
    }
}
```
- 직업 주입을 시켜서 config bean으로 등록해야 한다.

### UsernamePasswordAuthenticationFilter
```kt
addFilterBefore(ajaxLoginProcessingFilter(), UsernamePasswordAuthenticationFilter::class.java)
```
- 순서는 상관없음
- 어차피 요청에 따라 필터 실행 조건이 다르기 때문에 순서는 상관 없음
- 인증 Filter를 등록할 때 결국 요청 조건에 따라 필터가 실행되도록 잘 구현하기만 하면 된다.

### @Component로 빈 등록
```kt
@Override
public void afterPropertiesSet() {
   Assert.notNull(this.authenticationManager, "authenticationManager must be specified");
}
```
- `@Component`로 빈 등록하면 `AbstractAuthenticationFilter`의 이게 먼저 실행
- authenticationManager 저장하지 못하고 있음
- **스프링 시큐리티에서 필터를 생성하고 등록할 때 대부분 빈으로 등록하지 않음**

<br>

## :pushpin: AjaxAuthenticationProvider
```
org.apache.http.client.ClientProtocolException
```
- 위 에러는 POST 요청시 url 내용이 없어서 발생할 수 있음

### 주의점 1. 다른 config 파일 내 같은 bean 메소드
```kt
// AjaxSecurityConfig.kt
@Bean
fun ajaxFilterChain(http: HttpSecurity): SecurityFilterChain {
    return http
        .antMatcher("/api/**")
        .authorizeRequests()
        .anyRequest().authenticated()

        .and()
        .addFilterBefore(ajaxLoginProcessingFilter(), UsernamePasswordAuthenticationFilter::class.java)

        .csrf().disable()
        .build()
}
```
- 다른 config 파일에서 기존 설정 파일에서의 `filterChain` fun 이름과 중복되면 안된다.
- 만약 같은 이름의 bean 메소드를 지정하면 Order 순서에 따라 뒤에 설정되는 filterChain 정보가 등록이된다.
  - `addFilterBefore` 등록된 내용은 없어지게 됨

### 주의점 2. 다른 AuthenticationManager 객체 참조
```kt
// 초기화 때 생성된 AuthenticationManager
val authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class)
authenticationManagerBuilder.authenticationProvider(ajaxAuthenticationProvider());

// 위와 다른 AuthenticationManager 객체
authenticationConfiguration.authenticationManager
```
```kt
// AjaxSecurityConfig.kt
@Bean
fun authenticationManager(): AuthenticationManager {
  val authenticationManager = authenticationConfiguration.authenticationManager as ProviderManager
  authenticationManager.providers.add(ajaxAuthenticationProvider)
  return authenticationManager
}
```
- **이부분이 아직 와닿지 않는다.**

<br>

## :pushpin: 인증 및 인가 예외 처리

- `FilterSecurityInterceptor`
  - 여기서 인가 처리 담당(어떤 자원에 접근했을 때 접근 가능한 사용자인지 검증)
  - 인증을 받지 않은 사용자(익명 사용자)가 접근한 경우 -> 인증을 받도록 예외처리
  - 인증 받은 사용자가 허가 받지 않은 자원에 접근한 경우
  - 예외 발생시 `ExceptionTranslationFilter`로 인가 예외를 보낸다.
- `ExceptionTranslationFilter`
  - 두 가지 경우에 대한 인가 예외처리를 담당
    - 익명 사용자가 접근한 경우 -> 다시 인증 받도록 로그인 페이지로 유도하는 등의 처리
    - 인증 받은 사용자가 접근한 경우 -> 접근 불가능하다는 메시지 응답

```kotlin
// AjaxSecurityConfig.kt
http
  .exceptionHandling()
  .authenticationEntryPoint(ajaxLoginAuthenticationEntryPoint)  // 인증받지 않은 사용자에 대한 인가 예외 처리
  .accessDeniedHandler(ajaxAccessDeniedHandler)                 // 인증 받은 사용자의 허용받지 않은 자원 접근에 대한 예외 처리
```

## :pushpin: Custom DSL
- custom DSL 사용하는 이유는 한 곳에서 관련성있는 custom security 설정을 관리하기 위함
  - ex. AjaxLogin 관련 설정들을 하나의 클래스 파일 안에서 관리 가능
- `AjaxLoginConfigurer` 여기에 구현
- `AbstractConfiguredSecurityBuilder`.`configure()` 여기서 설정
  - `SecurityConfigurer`: configurer 최상위 인터페이스
  - `init` -> `configure`로 이루어짐