# Chap 6) 실전프로젝트 - 인가 프로세스 DB 연동 웹 계층 구현

- 주의 사항
  - `data.sql`이 application 실행시 spring security 설정보다 늦게 동작
  - security metadata에 (요청정보 to 권한정보) DB 조회 후 설정하는 부분에서 데이터 조회 X
  - 임시방편으로 최초 애플리케이션 실행시
    - `ddl-auto: create`
    - `sql.init.mode: always`
  - 이후 애플리케이션 재실행시
    - `ddl-auto: update`
    - `sql.init.mode: never`

<br>

## :pushpin: 코틀린 프로젝트로 변환 중 이슈

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

<br>

## :pushpin: FilterInvocationSecurityMetadataSource
- custom FilterSecurityInterceptor 로 적용해서 Config 설정
- UrlFilterInvocationSecurityMetadataSource 3번 호출됨
  - `/mypage` 뿐만 아니라 `/js` 파일들도 호출됨

### addFilterBefore
- `SecurityConfig`에서 addFilterBefore 설정
```kotlin
.and()
.addFilterBefore(customFilterSecurityInterceptor(), FilterSecurityInterceptor::class.java)
```
- 이런 식으로 기존 FilterSecurityInterceptor 앞에 custom filter 설정
- FilterSecurityInterceptor 두 개가 등록

```kotlin
// FilterSecurityInterceptor
if (isApplied(filterInvocation) && this.observeOncePerRequest) {
    // filter already applied to this request and user wants us to observe
    // once-per-request handling, so don't re-do security checking
    filterInvocation.getChain().doFilter(filterInvocation.getRequest(), filterInvocation.getResponse());
    return;
}
```
- 앞에서 `FilterSecurityInterceptor` 호출된 적이 있으면 더이상 해당 Filter는 사용하지 않음

### FilterSecurityInterceptor 필터 적용 안하는 경우

```kotlin
.authorizeRequests()
.anyRequest().authenticated()
```
- 이것에 의해 home 화면 진입 때부터 인증을 요구하게 된다.
- customFilterSecurityInterceptor 를 통한 metadatasource를 따로 지정하지 않으면 다른 requestMap으로 설정
  - `/login` -> permitAll (`loginPage("/login")` 설정)
  - `/login_proc` -> permitAll (`loginProcessingUrl("/login_proc")` 설정)
  - `any request`
  - `/` 루트로 이동하면 any request에 걸려서 인증 요구를 받게 된다. (`/login` 페이지로 이동)
- metadatasource 따로 지정하게 되면 설정된 내용만 requestMap에 등록
  - any request 부분이 없고 딱 설정된 부분만 적용
  - 그래서 설정된 요청정보 - 권한정보에 걸리지 않는 요청내용에 대해서는 null로 반환
```kotlin
// AbstractSecurityInterceptor
Collection<ConfigAttribute> attributes = this.obtainSecurityMetadataSource().getAttributes(object);
if (CollectionUtils.isEmpty(attributes)) {
    //...
    return null; // no further work post-invocation
}
```
- `attributes`가 null로 반환되면 더이상 인가 과정을 진행하지 않는다.

<br>

## :pushpin: 웹 기반 인가처리 실시간 반영
- resource, role 정보가 변경되면 실시간으로 `metadatasource` `requestMap`에 반영되어야 한다.
- resources 생성, 삭제시 `reload` 메소드 적용 

### 주의점
- `@ManyToMany` 이슈(JPA)
```sql
delete from role_resources where resource_id=?;
insert into role_resources(resource_id, role_id) 
values (?, ?);
```
- ManyToMany 로 연결되어 있는 resources, role
- resources entity: roleSet에 요청받은 새로운 role을 넣고 setter 변경
- 그렇게 되면 위와 같이 쿼리가 role_resources 매핑 테이블에서 resource_id 관련 데이터 전부 삭제
- 그 다음 새로운 데이터 insert 진행
- **매핑 테이블에서 연관 데이터 전부 삭제한다는 점에서 비효율적**

<br>

## :pushpin: 계층 권한 적용하기(RoleHierarchy)

### RoleHierarchy
- `RoleHierarchy` -> `RoleHierarchyImpl` 객체에 setHierarchy 메소드를 통해서 권한 계층정보를 전달해야 한다.
```text
ROLE_ADMIN > ROLE_MANAGER
ROLE_MANAGER > ROLE_USER
```
- 이런 식의 string 값으로 `RoleHierarchyImpl`에 전달해야 한다.
- 이렇게 되면 role 계층 정보에 의해 ROLE_ADMIN 권한만 가진 사용자는 하위 권한 모두 접근 가능

### 주의점
- RoleHierarchy Entity 설정할 때
  - parentName을 JoinColumn에 `referencedColumnName` 설정
  - 이 때 `referencedColumnName`으로 설정된 field는 serializable 해야 한다. ([링크](https://www.baeldung.com/jpa-entities-serializable#2-hibernate-joincolumn-annotation))

<br>

## :pushpin: 아이피 접속 제한하기 - CustomAddressVoter

인가 과정에서
- `FilterSecurityInterceptor`(`AbstractSecurityInterceptor`) - `attemptAuthorization` 
  - `AccessDecisionManager` - `decide` 
    - voter를 가지고 인가 허용 여부 체크 
    - 3개 구현체 `AffirmativeBased`, `ConsensusBased`, `UnanimousBased`
  - `AccessDecisionVoter` - `vote` 
    - `List<AccessDecisionVoter>` 형태로 Manager가 가지고 있음
    - 각각의 voter들은 기준을 가지고 `ACCESS_DENIED`, `ACCESS_GRANTED` 결정

이런 식으로 진행

### IpAddressVoter

- `AccessDecisionVoter` 상속
- 여기서 1차적으로 허가된 ip 주소인지 판별
- 허용된 ip address 경우 
  - `ACCESS_ABSTAIN` (보류, 추가 심의 진행) 
  - `AccessDecisionManager`에서 본래 voter들을 가지고 인가 과정 진행
  - 여기서 `ACCESS_GRANTED`로 해버리면 `AffirmativeBased` 기반에서는 본래의 인가처리 없이 통과되어버리는 사태발생
- 허용된 ip address X 경우 
  - `ACCESS_DENIED`가 아닌 `AccessDeniedException` 발생 시켜서 바로 접근 제한해야 함

그리고 강의에서는 `IpAddressVoter`에서 `WebAuthenticationDetails` 을 사용해서 remoteAddress를 가져오는데
```kotlin
// AjaxLoginProcessingFilter
val token = AjaxAuthenticationToken(accountDto.username, accountDto.password).apply {
  this.details = WebAuthenticationDetails(request)
}
```
Filter 차원에서 AuthenticationToken 생성할 때 WebAuthenticationDetails를 넣어야 한다.  
(안그러면 NullPointerException 발생)
