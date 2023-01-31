## :pushpin: API 개발 고급 - 컬렉션 조회 최적화

### 엔티티 직접 노출 & Dto 변환

N+1의 문제 발생
```kotlin
@GetMapping("/api/v2/orders")
fun orderV2(): List<OrderDto> {
    val orders = orderRepository.findAllByString(OrderSearch())
    // LAZY proxy 초기화
    return orders.map {
        OrderDto.of(it)
    }
}
```
이렇게 되면 Order Entity에 연관관계로 연결되어 있는 entity에 대해서 추가 조회 쿼리가 나가게 된다.  
```
Order
- Member
- OrderItem
    - Item
- Delivery
```

<br>

### fetch join 최적화

```kotlin
fun findAllWithItem(): List<Order> {
    return em.createQuery(
        """
            |select o from Order o
            |    join fetch o.member m
            |    join fetch o.delivery d
            |    join fetch o.orderItems oi
            |    join fetch oi.item i
        """.trimMargin(),
        Order::class.java
    ).resultList
}
```
위의 방식으로 fetch join을 하게 되면 order data가 뻥튀기 된다.
```
order id: 4 > order_item id: 6
order id: 4 > order_item id: 7
order id: 11 > order_item id: 13
order id: 11 > order_item id: 14
```
fetch join은 실제 쿼리가 나갈 때 join으로 나가게 되는데 실제 쿼리 결과는 order 데이터가 각각 2개씩 나오게 된다.  
JPA는 이를 그대로 반영해서 Entity에도 똑같은 Order 내용이 2개가 나오게 되는 것을 확인할 수 있다.
```text
order ref = io.brick.jpabook.jpashop.domain.Order@1cde0081 / id = 4
order ref = io.brick.jpabook.jpashop.domain.Order@1cde0081 / id = 4
order ref = io.brick.jpabook.jpashop.domain.Order@215d91f8 / id = 11
order ref = io.brick.jpabook.jpashop.domain.Order@215d91f8 / id = 11
```
이를 방지하기 위해 JPQL distinct 문법을 사용하면 된다.
```kotlin
fun findAllWithItem(): List<Order> {
    return em.createQuery(
        """
            |select distinct o from Order o
            |    join fetch o.member m
            |    join fetch o.delivery d
            |    join fetch o.orderItems oi
            |    join fetch oi.item i
        """.trimMargin(),
        Order::class.java
    ).resultList
}
```
- 이렇게 해도 실제 쿼리 조회시 중복제거는 발생하지 않고 이전과 그대로다.  
  - select distinct order_id, member_id, ... from order ... 생각해보면 된다.  
  - distinct 뒤의 칼럼 데이터들이 모두 같아야 중복을 제거하는 특징
- 하지만 application 단계에서 JPQL distinct는 쿼리 조회 결과를 보고 중복이 있으면 이를 알아서 제거해준다.  
  - JPQL root from entity에서 중복이 있으면 알아서 제거해준다.
- 결국 JPQL fetch join으로 원하는 entity만 미리 가져와서 쿼리 하나로 관련 데이터 모두 조회 가능

> **치명적 단점**  
> 페이징 기능 사용을 못함  
> 만약 페치 조인에 페이징 기능을 적용하면 애플리케이션 단에서 WARN 경고를 낸다.  
> applying in memory >> 실제 쿼리상에서 join된 결과를 페이징 처리할 수 없으니 어쩔 수 없이 메모리로 모든 데이터를 올려서 페이징 처리  
> 만약 데이터 규모가 만개 단위면 OOM 발생 가능

<br>

### 페치 조인에서의 페이징과 한계 돌파

- 우선 ToOne 관계의 연관 entity에 대해서는 fetch join으로 가져온다. (어차피 데이터 뻥튀기 발생할 일 없음)
- 그리고 리스트 연관관계의 entity에 대해서는 BatchSize로 가져온다.
```yml
spring:
  jpa:
    properties:
      hibernate:
        default_batch_fetch_size: 100 # batch size 설정
```
- 이렇게 되면 order 조회로 우선 가져오고 그다음 order_item > item 을 batch size 만큼 in query로 조회하게 된다.
- 즉 batch size 설정시 각각의 order_item_id 에 따라 쿼리 하나씩 생성해서 요청하는 것이 아니라 in query를 통해 bulk하게 조회

**BatchSize Entity 설정**
```kotlin
// Order
@BatchSize(size = 100)
@OneToMany(mappedBy = "order", cascade = [CascadeType.ALL])
var orderItems: MutableList<OrderItem> = ArrayList()

// Member
@BatchSize(size = 100)
@Entity
class Member protected constructor(...)
```
- `@ManyToOne`에서는 연관관계 대상이 되는 Entity class 단에 설정(Order 입장에서는 Member)
- `@OneToMany`에서는 해당 필드에다가 설정

#### 정리
OneToMany를 가져오는데 있어서 N+1 문제가 발생(LAZY)  
 - fetch join으로 OneToMany 연관관계 대상을 쿼리 하나로 가져옴(장점)
 - 하지만 데이터 중복 문제(데이터 뻥튀기 문제), 페이징 처리 불가 문제 발생  

 **batch size**를 통한 각각의 entity in query 조회
 - 우선 Order만 조회(**필요시 페이징 처리도 가능**)
 - **연관관계의 entity(LAZY로 묶여있음)에 대해서 batch size 설정된 대로 id 값들을 가지고 in query 조회**
 - 즉 batch size 설정을 잘하면 `N + 1` > `1 + 1`도 가능(물론 batch size를 넘어가는 데이터 량이면 쿼리 개수 증가)
 - fetch join으로 발생했던 데이터 뻥튀기 문제도 해결 가능 > **DB 데이터 전송량 감소**
   - ex. Order 1개에 OrderItem 100개 연결되어 있으면 Order 데이터도 100개가 조회되는 중복문제 발생

<br>

### JPA에서 DTO 직접 조회(JPQL) & 컬렉션 조회 최적화

- `OrderQueryRepository.findOrderQueryDtos()` 메소드 내용 참고
- JPQL에서 DTO 직접 조회
- 여기서 N+1 문제는 해결하지 못한 상황
  - order 조회 후 각 order에 대한 orderItem 조회

#### 컬렉션 조회 최적화
- 각 order에 대한 orderItem 조회 시 in query 사용(order_id collection 사용)
```kotlin
return em.createQuery(
    """
            |select new io.brick.jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)
            |from OrderItem oi
            | join oi.item i
            |where oi.order.id in :orderIds
        """.trimMargin(),
    OrderItemQueryDto::class.java
)
    .setParameter("orderIds", orderIds)
    .resultList
```
- 이렇게 하면 order 조회시 두 번의 쿼리를 사용해서 조회 가능
  - order 조회 & orderItem in query 조회

<br>

### JPA에서 DTO로 직접 조회, 플랫 데이터 최적화

- 쿼리 하나로 Order와 관련있는 연관관계 Entity 모두 조회해서 DTO 반환 가능 (OrderQueryDto)
- controller단에서 기존 OrderQueryDto 형태로 최종 반환하고 싶을 때는 kotlin `groupBy` 사용
  - 유의해야할 점은 `groupBy`를 객체 기준으로 하기 때문에 Object의 `equals & hashcode` 기준으로 하게 됨
  - 이를 overriding 해야되기에 kotlin의 data class 기능을 사용해야 함
- 장점
  - 쿼리 한 번에 관련 엔티티까지 조회 가능
- 단점
  - 쿼리 한 번이지만 데이터 중복 데이터가 추가되기 때문에 상황에 따라 성능이 더 느릴 수 있음
  - 애플리케이션 단에서 추가 작업이 크다.
  - 페이징 처리자체 불가능(데이터 뻥튀기 문제) - Order 기준으로

### 정리
**권장 순서**
1. Entity 조회 방식 우선 접근
   1. 페치 조인 쿼리 수 최적화
   2. 컬렉션 최적화
      1. 페이징 필요 - `hibernate.default_batch_fetch_size`, `@BatchSize` 최적화
      2. 페이징 필요 X -> 페치 조인 사용
2. 엔티티 조회 방식으로 해결이 안되면 DTO 조회 방식 사용
3. DTO 조회 방식으로 해결이 안되면 NativeSQL or 스프링 JdbcTemplate 사용

> 성능 최적화와 코드 복잡도 사이에서 줄타기를 해야 한다.

DTO 방식으로 조회(V4 ~ 6)하는 방식은 코드복잡도를 가져오게 된다.  
JPA 기능(batch size 등)으로 하면 코드를 건들지 않고 설정만으로 성능 최적화를 달성할 수 있다.
  
<br>

DTO 방식 특징
- V4: 로직이 명료함 (order 전체조회 -> 각 order의 orderItem 리스트 조회)
- V5: 성능 최적화 (order 전체조회 -> order's id 리스트 추출해서 orderItem in query 조회)
- V6 
  - 한 방 쿼리로 DTO 조회 가능, 하지만 order 기준으로 성능 최적화할 수 없다.
  - 데이터 뻥튀기 때문에 V5가 성능이 더 좋을 확률이 높다.