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