# AOP 방식의 인가 프로세스

```kotlin
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
class SecurityConfig {
    //...
}
```
`prePostEnabled = true, securedEnabled = true` 옵션 활성화 여부에 따라  
거기에 맞는 AOP 전용 MetadataSource 객체를 설정해준다.  
(`GlobalMethodSecurityConfiguration` 여기서 확인 가능)

- prePostEnabled > `@PreAuthorize`, `@PostAuthorize`
- securedEnabled > `@Secured`