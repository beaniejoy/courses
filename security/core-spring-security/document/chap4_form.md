# Chap 4) Form 인증 구현

## 실전 프로젝트 생성

```kt
@Bean
fun filterChain(http: HttpSecurity): SecurityFilterChain {
    return http
        .authorizeRequests()
        .antMatchers("/", "/users").permitAll()
        .antMatchers("/mypage").hasRole("USER")
        .antMatchers("/messages").hasRole("MANAGER")
        .antMatchers("/config").hasRole("ADMIN")
        .anyRequest().authenticated()

        .and()
        .formLogin()

        .and().build()
}
```
- 변경된 Spring Security 설정 방식
- overriding 방식이 아닌 `FilterChain` 구현을 통한 Bean 등록 방식으로 변경

```kt
@Bean
fun userDetailsService(): InMemoryUserDetailsManager {
    val password = passwordEncoder().encode("1111")

    return InMemoryUserDetailsManager(
        listOf(
            User.withUsername("user").password(password).roles("USER").build(),
            User.withUsername("manager").password(password).roles("MANAGER").build(),
            User.withUsername("admin").password(password).roles("ADMIN").build()
        )
    )
}
```
- 변경된 In Memory 사용자 등록방법 (`InMemoryUserDetailsManager`)

<br>

## WebIgnore 설정
- js, css, image 파일 등 보안 필터를 적용할 필요가 없는 리소스를 설정

```kt
@Bean
fun webSecurityCustomizer(): WebSecurityCustomizer {
    return WebSecurityCustomizer { web ->
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations())
    }
}
```
- `antMatchers().permitAll()`과 다른 점은 보안필터를 아예 거치지 않는다는 것
- 비용적인 측면에서 ignoring이 더 좋다.

```
2022-10-17 01:00:51.680  WARN 5168 --- [  restartedMain] o.s.s.c.a.web.builders.WebSecurity       
: You are asking Spring Security to ignore org.springframework.boot.autoconfigure.security.servlet.StaticResourceRequest$StaticResourceRequestMatcher@36bfe991. 
This is not recommended 
-- please use permitAll via HttpSecurity#authorizeHttpRequests instead.
```
- ignoring 말고 `permitAll()`로 하라고 경고가 나오긴 함

<br>

## PasswordEncoder
```kt
@Bean
fun passwordEncoder(): PasswordEncoder {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder()
}
```
- `DelegatingPasswordEncoder`
    - 여러 개 PasswordEncoder 유형을 선언한 뒤, 상황에 맞게 선택해서 사용할 수 있도록 지원하는 Encoder

<br>

## CustomUserDetailsService

```kt
// Collection<? extends GrantedAuthority> authorities
// TODO 알아보기
authorities: MutableCollection<out GrantedAuthority>
```
- kotlin에서 MutableCollection -> out 설정 (이유 찾아보기)

### Config 설정
```java
@Autowired
private UserDetailsService userDetailsService;

@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService);
}
```
- 기본 Security에서는 Overriding 통해서 userDetailsService를 등록
- **최근 버전에서는 UserDetailsService customizing한 클래스를 빈 등록하면 자동으로 그것을 사용**

<br>

## CustomAuthenticationProvider
- 여기도 5.7 바뀐 설정에서는 `@Component` 등록만으로 스프링 시큐리티에 `AutheticationProvider` 등록
- 이 때 인증 과정을 거치게 되면 `ProviderManager`를 통해 자식부터 등록된 `AutheticationProvider` 탐색
- 없으면 부모 `ProviderManager`로 올라가게 되는데 거기에 등록된 `CustomAuthenticationProvider`도 `supports` 체크 과정 거치게된다.
- `supports` 체크결과 해당하는 Provider면 이것을 통해 authenticate 과정을 위임하게 된다.

<br>

## 인증 부가 기능
- `WebAuthenticationDetails`
- `AuthenticationDetailsSource`

### WebAuthenticationDetails
- 인증 처리시 사용자가 입력한 username, password 이외의 parameter를 받아서 사용 가능
- `request.getParameter("param1")`
```kotlin
protected void setDetails(HttpServletRequest request, UsernamePasswordAuthenticationToken authRequest) {
    authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
}
```
- `UsernamePasswordAuthenticationFilter` 여기서 `UsernamePasswordAuthenticationToken`을 얻고
- token에 setDetails 작업 진행 -> 여기서 `AuthenticationDetailsSource` 사용

<br>

## CustomAuthenticationSuccessHandler

```kotlin
return http
    //...
    .authenticationDetailsSource(authenticationDetailsSource)
    .defaultSuccessUrl("/")
    .successHandler(customAuthenticationSuccessHandler)
    .permitAll()
    .and().build()
```
```
Q) 아래 다른분이 SuccessHandler가 동작하지 않으면
defaultSuccessURl위치를 successHandler위쪽으로 올리라고 하셔서 그대로 수정하니 동작했습니다.
이때 defaultSuccesURl의 설정 위치가 successHandler의 커스터마이징한 SuccessHandler의 동작에 영향을 주는지 궁금합니다. 

A) 말씀하신 것처럼 API 설정 위치에 따라 적용되는 결과가 달라집니다.
successHandler()와 defaultSuccessURl()를 설정하게 되면 
스프링 시큐리티가 초기화 되면서 FormLoginConfigurer 의 부모클래스인 
AbstractAuthenticationFilterConfigurer의 successHandler 속성에 저장이 되고 
다시 UsernamePasswordAuthenticationFilter 의 successHandler 속성에 반영이 됩니다.
로그인에 성공을 하게 되면 UsernamePasswordAuthenticationFilter에 저장된 successHandler 가 호출이 되는 구조입니다.
그렇기 때문에 API 설정이 아래에 위치할 수록 위에 위치한 설정을 덮어쓰게 되어서 그런 결과가 나타나게 됩니다.
```

<br>

## CustomAuthenticationFailureHandler
```kt
setDefaultFailureUrl("/login?error=true&exception=${exception.message}")
```
- spring security는 url로 설정한 string 전체를 하나의 경로로 인식함
- 이부분에 대해서 config에 따로 permitAll 처리해야한다.
```kotlin
return http
    .authorizeRequests()
    .antMatchers("/", "/users", "/error", "user/login/**", "/login*").permitAll()
    .and().build()
```

<br>

## 인증 거부 처리 - Access Denied
- 인가 예외 `AccessDeniedException`에 대해서 `ExceptionTranslationFilter`가 받아서 처리
- 인증 시도 예외 - 해당 인증을 처리하는 Filter가 처리
- 여기서 기록해둘만한 것은 `CustomAccessDeniedHandler`에서 `response.sendRedirect("/denied")`를 했는데  
  페이지 기준으로 `A` -> `B`(인가 Denied) -> redirect -> `error page` -> 뒤로가기 -> `A`
- redirect는 사용자가 다시 요청을 보내는 것으로 알고 있었는데 페이지 뒤로가기 하면 B 진입 전인 A 페이지로 이동
    - **:pushpin: 이부분이 와닿지 않음**




