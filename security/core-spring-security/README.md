# Core Spring Security (Kotlin)

- kotlin, gradle 버전 core-spring-security 강의 내용
- [강의(스프링 시큐리티 inflearn) repository](https://github.com/onjsdnjs/corespringsecurity)

<br>

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
