# OSIV
- Open Session In View
- Open EntityManager in View (JPA)

<br>

## :pushpin: 개념
```yaml
spring.jpa.open-in-view: true (default)
```
- `@Transactional`에 의해 시작된 DB 커넥션을 가지고 영속성 컨텍스트가 1:1 매핑됨
- OSIV가 활성화되어 있으면 해당 `@Transactional` 걸린 메소드를 떠나 클라이언트에 최종 응답될 때까지 영속성 컨텍스트, DB 커넥션이 살아있음
- **이렇게 한 이유는 지연로딩(장점)**
  - View Template이나 API Controller단에서도 지연로딩 기능을 사용하기 위함(영속성 컨텍스트, DB 커넥션 필요)
- 하지만 이렇게 하면 치명적인 단점이 존재

### 단점
- 너무 오랜시간동안 DB 커넥션 리소스를 사용하고 있기 때문에 실시간 트래픽이 많은 서비스에서 커넥션 고갈될 가능성이 높아짐
- ex) API중에 3초 이상 시간이 소요되는 것이 있으면 그 시간 동안 커넥션을 반환받지 못하는 것임  
API 응답될 때까지 커넥션을 잡아먹고 있는 것임

## :pushpin: OSIV 비활성화

```yaml
spring.jpa.open-in-view: false
```
- 이렇게 되면 `@Transactional` 시작지점에서 DB 커넥션과 PC 생성하고 끝나는 지점에서 커넥션 반환 PC 닫기가 이루어진다.

### 단점
- 트랜잭션 안에서 모든 지연로딩을 처리해야 한다.
- view template 단에서 지연로딩 기능을 사용할 수 없음
- 트랜잭션 끝나기전에 지연로딩을 강제로 호출하거나 fetch join해서 완성된 형태를 가져와야함
```
org.hibernate.LazyInitializationException: 
could not initialize proxy [io.brick.jpabook.jpashop.domain.Member#1] 
- no Session
```
- open-in-view false 한 상황에서 Controller단에서 지연로딩을 시도하고자 하면 위의 에러 발생

<br>

## :pushpin: OSIV 비활성화에 대한 대처
- Command, Query 부분 분리 (CQS, Command Query Separation)
- ex) `/api/v3/orders` API 참고
  - OrderQueryService로 지연로딩 부분을 분리, Tx으로 묶는다.

### 핵심 비즈니스 로직과 화면용 API 로직

- 화면 조회용 api는 화면에 맞춰야 하기에 수정도 자주 발생 가능
- 핵심 비즈니스 로직은 거의 안바뀜
- 이 둘을 분리하는 것이 좋다.
```text
OrderService: 핵심비즈니스용
OrderQueryService: 화면 조회용 API 로직(주로 readOnly 사용)
```

<br>

## :pushpin: 정리

OSIV 장점을 고려했을 때(view template에서도 사용 가능) 어드민 단에서 적용하면 좋을 듯  
**고객 서비스의 트래픽이 많은 실시간 API는 OSIV를 기본적으로 비활성화하는 것이 좋다.**

