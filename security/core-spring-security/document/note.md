# 기록

<br>

## WebIgnore
```
2022-10-17 01:00:51.680  WARN 5168 --- [  restartedMain] o.s.s.c.a.web.builders.WebSecurity       
: You are asking Spring Security to ignore org.springframework.boot.autoconfigure.security.servlet.StaticResourceRequest$StaticResourceRequestMatcher@36bfe991. 
This is not recommended 
-- please use permitAll via HttpSecurity#authorizeHttpRequests instead.
```

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
스프링 시큐리티가 초기화 되면서 FormLoginConfigurer 의 부모클래스인 AbstractAuthenticationFilterConfigurer의 successHandler 속성에 저장이 되고 
다시 UsernamePasswordAuthenticationFilter 의 successHandler 속성에  반영이 됩니다.
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

```