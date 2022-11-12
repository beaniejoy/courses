# Chap 6) 실전프로젝트 - 인가 프로세스 DB 연동 웹 계층 구현

## 코틀린 프로젝트로 변환 중 이슈

- 기존 `Account`에 `Resources`, `Role` 엔티티 추가

### 주의점
- `AjaxAuthenticationFilter`에 의해 인증 프로세스 거친 후 `AjaxAuthenticationSuccessHandler`에 이슈 
  - 인증 받은 `authentication` 객체에서 `principal` 을 `Account`로 type casting
  - Account 객체를 `ObjectMapper`로 serializing 진행
  - 직렬화 과정 중에 Account - Role 에서도 getter 발생
  - `Role`의 `resourcesSet`, `accounts`는 LAZY로 가져온 내용이라 트랜잭션 과정에서 프록시로 가져왔음
  - 하지만 success handler에서는 이미 트랜잭션이 끝난 상황이므로 프록시 기능 사용 불가
  - `@JsonIgnore`를 통해서 json 직렬화 과정에서 제외시켜야 함
  - 만약에 이후에 `@JsonIgnore`로 다른 문제 발생시 successHandler에서 response dto로 따로 변환해서 직렬화해야할 듯

<br>

## :pushpin: 인가 처리 주요 아키텍처

### 인가 프로세스 과정
- `SecurityInterceptor`
  - 인증정보: `Authentication`
  - 요청정보: `FilterInvocation`
  - 권한정보: `List<ConfigAttribute>`
- 위 3개 정보를 받아서 `AccessDecisionManager`로 전달

### 프로세스 구현체
- `ExpressionBasedFilterInvocationSecurityMetadataSource`
  - 초기 애플리케이션 실행시 설정된 권한정보를 가지고 map 형태로 반환
    - `LinkedHashMap<RequestMatcher, Collection<ConfigAttribute>>`
    - ex) `/user`: `hasRole(ROLE_USER)`
  - `DefaultFilterInvocationSecurityMetadataSource`에 requestMap 전달
- `FilterSecurityInterceptor`
  - 인가 처리를 담당하는 security filter
  - invoke 메소드에서 진행 
  - `FilterInvocation` -> **찾아보기**
- `DefaultFilterInvocationSecurityMetadataSource`
  - 위에 초기화 과정에서 받아온 requestMap 정보를 가지고 있는 상황
  - `getAttributes`
    - 여기서 애플리케이션 실행단계에서 생성해두었던 mapping 정보를 기반으로 자원정보에 대한 권한정보 추출하는 과정 진행

### DB 방식의 인가 프로세스 구현
- 위의 방식은 Config 클래스에서 미리 설정한 내용으로 하는 인가 처리 방식
- `SecurityMetadataSource` 이것을 구현하면 됨
  - `FilterInvocationSecurityMetadataSource`: url 방식 인가
  - `MethodSecurityMetadataSource`: Method 권한 정보 추출