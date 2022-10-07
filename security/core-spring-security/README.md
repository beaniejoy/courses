# Core Spring Security (Kotlin)

- kotlin, gradle 버전 core-spring-security 강의 내용
- [강의(스프링 시큐리티 inflearn) repository](https://github.com/onjsdnjs/corespringsecurity)

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
