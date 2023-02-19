# Spring Data JPA

- [김영한님 실전! 스프링 데이터 JPA](https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-%EB%8D%B0%EC%9D%B4%ED%84%B0-JPA-%EC%8B%A4%EC%A0%84/)

## :pushpin: Overview
- gradle 의존 관계 확인
```shell
$ ./gradlew dependencies --configuration compileClasspath
```

<br>

## :pushpin: 공통 인터페이스 분석

```text
JpaRepository > PagingAndSortingRepository > CrudRepository > Repository
```
- `spring-data-commons`
  - `PagingAndSortingRepository`
  - `CrudRepository`
  - `Repository`
- `spring-data-jpa`
  - `JpaRepository`

<br>

## :pushpin: 쿼리 메소드 기능
 
### 메소드 이름으로 쿼리 생성

- select: `find...By`, `read...By`, `get...By`
- count: `count...By`
- exists: `exists...By`
- distinct: `findDistinct`, `findMemberDistinctBy`
- limit: `findFirst3`, `findFirst`, `findTop3`

```kotlin
fun findHelloBy(): List<Member>
fun findTop3HelloBy(): List<Member>
```
위 방식대로 사용하면 Hello는 무시되고 전체조회와 같이 나온다.

### JPA NamedQuery

- JPQL 설정
```kotlin
// Member.kt
@Entity
@NamedQuery(
    name = "Member.findByUsername",
    query = "select m from Member m where m.username = :username"
)
class Member {
    //...
}
```
```kotlin
// MemberJpaRepository.kt
fun findByUsername(username: String): List<Member> { 
    return em.createNamedQuery("Member.findByUsername", Member::class.java)
        .setParameter("username", username)
        .resultList
}
```
- Spring Data JPA repository 설정
```kotlin
// MemberRepository.kt
@Query(name = "Member.findByUsername")
fun findByUsername(@Param("username") username: String): List<Member>
```
여기서 `@Query(name = "Member.findByUsername")` 생략 가능  
**Data JPA가 알아서 `Member.[methodName]`으로 된 NamedQuery를 찾고 없으면 메소드 이름 생성방법으로 쿼리 작성한다.**

> 순서: NamedQuery 먼저 찾고 없으면 > Method Name Query 

하지만 실무에서는 NamedQuery 방식 거의 사용 X (관리가 힘듬)  
- **유일한 장점**  
  - application 최초 실행 loading 시점에서 NamedQuery를 파싱
  - 이 과정에서 작성된 JPQL에서 문법오류 찾아내준다.
  - `where m.userwrongname = :username` > 이걸 찾아내 준다.
  - (`createQuery` 통한 JPQL에서는 못 찾아냄, 실제 사용될 때 오류 검출) 

### @Query, Repository method 직접 지정

```kotlin
@Query("select m from Member m where m.username = :username and m.age = :age")
fun findUser(@Param("username") username: String, @Param("age") age: Int): List<Member>
```
- `JpaRepository` 상속받은 인터페이스 메소드에 `@Query`로 직접 지정 가능
- 장점은 NamedQuery와 비슷하게 application loading 시점에 JPQL 파싱을 통한 문법 오류 검출가능
  - 잘못 입력된 where 칼럼이름도 검출
- **사실 위의 `@Query`는 이름이 없는 NamedQuery와 같다.**

### 반환 타입
- Spring Data JPA는 반환타입을 유연하게 처리할 수 있게 해준다.
```kotlin
fun findListByUsername(username: String): List<Member> // 컬렉션
fun findMemberByUsername(username: String): Member? // 단건
fun findOptionalByUsername(username: String): Optional<Member> // Optional wrapping
```
- list 컬렉션 반환에서 데이터가 없는 경우 size 0인 empty list 반환해준다.
- 단건 조회인데 데이터 조회 결과가 2개 이상이면 에러 발생
  - `NonUniqueResultException` (JPA exception)
  - `IncorrectResultSizeDataAccessException` (springframework exception): 이걸로 변환하게 된다.

> spring framework에서는 하부 repository 기술에 의존하는 것이 아닌 spring 추상화된 interface로 변환  
> 그래서 MongoDB, JPA, Redis 등의 하부 repository 기술이 변경되어도 코드에서는 변경지점이 없게 됨

### Spring Data JPA Paging & Sorting
- JPQL 처리
```kotlin
fun findByAge(age: Int, offset: Int, limit: Int): List<Member> {
    return em.createQuery("select m from Member m where m.age = :age order by m.username desc", Member::class.java)
        .setParameter("age", age)
        .setFirstResult(offset)
        .setMaxResults(limit)
        .resultList
}

fun totalCount(age: Int): Long {
    return em.createQuery("select count(m) from Member m where m.age = :age", Long::class.java)
        .setParameter("age", age)
        .singleResult
}
```
`setFirstResult`, `setMaxResults`를 이용해 offset, limit 개수를 설정  
여기서 totalCount를 조회하기 위한 count query 별도 구성해야 한다.(번거로움)

- Spring Data JPA 처리
```kotlin
// MemberRepository
fun findByAge(age: Int, pageable: Pageable): Page<Member>
fun findWithSliceByAge(age: Int, pageable: Pageable): Slice<Member>
fun findWithListByAge(age: Int, pageable: Pageable): List<Member>
@Query(value = "select m from Member m left join m.team t", countQuery = "select count(m) from Member m")
fun findWithCountQueryByAge(age: Int, pageable: Pageable): List<Member>
```
Spring Data JPA는 `Pageable` interface와 `Page`, `Slice` 인터페이스를 제공해준다.

```kotlin
val members = memberRepository.findByAge(age, pageRequest)
assertThat(members.content.size).isEqualTo(3)
assertThat(members.totalElements).isEqualTo(5)
assertThat(members.number).isEqualTo(0)
assertThat(members.totalPages).isEqualTo(2)
assertThat(members.isFirst).isTrue
assertThat(members.hasNext()).isTrue
```
여러가지 paging 관련 API 제공  
`Slice`로 반환하면 limit 크기를 설정한 것보다 +1만큼 조회(더보기 기능 관련해서 적용할 수 있음)  
`Slice`로 하면 count query는 날라가지 않고 본 쿼리만 날라가게 된다.(성능 최적화)  
`List`로 반환할 수 있는데 이 때에도 count query X (limit 개수만큼 잘라서 조회하고 싶을 때)  

### 벌크성 수정 쿼리

- 주의해야할 점
```kotlin
val resultCount = memberRepository.bulkAgePlus(20)

val result = memberRepository.findByUsername("member5")
val member5 = result[0]
println("member5 = $member5") // 31로 update 처리 안됨
```
벌크 연산은 DB에 바로 쿼리를 실행해버리기 때문에 JPA 영속성 컨텍스트를 거치지 않는다.  
그래서 member5 조회했을 때 update 전 값이 나오게 된다.
```kotlin
em.flush()
em.clear()

// 위 대신에
@Modifying(clearAutomatically = true)
@Query("update ...")
fun bulkAgePlus(@Param("age") age: Int): Int
```
**벌크성 연산 직후에 entity manager를 비워줘야 한다.**  
JDBC, MyBatis 이용한 변경 쿼리 적용도 마찬가지다.(clear 해줘야 한다.)

- save와 관계  
**JPQL update** 쿼리는 그 이전에 JPA repository save 처리한 것이 있다면 PC에 있는 캐시된 내용을 갖고  
먼저 db insert 진행한 다음에 update 처리를 하게 된다. (JPA 변경감지와 다르다.)

### @EntityGraph
- member - team: lazy fetch 연결
- N+1 문제 발생 가능
```kotlin
// fetch join 이용한 N+1 문제 해결
@Query("select m from Member m left join fetch m.team")
fun findMemberFetchJoin(): List<Member>
```
JPQL 이용한 fetch join으로 문제 해결 가능  
하지만 매번 JPQL을 통해 fetch join 설정해야하는 번거로움 존재

```kotlin
// EntityGraph 이용한 N+1 문제 해결
@EntityGraph(attributePaths = ["team"])
override fun findAll(): List<Member>
```
이런 식으로 `@EntityGraph`를 이용해 fetch join 조회 가능  
```kotlin
@EntityGraph(attributePaths = ["team"])
@Query("select m from Member m")
fun findMemberEntityGraph(): List<Member>

@EntityGraph(attributePaths = ["team"])
fun findEntityGraphByUsername(@Param("username") username: String): List<Member> // find...ByUsername ...에 아무거나 상관없음
```
위와 같이 사용할 수 있음

```kotlin
// Member
@NamedEntityGraph(name = "Member.all", attributeNodes = [NamedAttributeNode("team")])
class Member protected constructor(
    //...
) { 
  //... 
}
```
`@NamedEntityGraph` 사용해서 미리 EntityGraph를 설정할 수 있다.

### JPA hint & Lock

변경감지(dirty checking)만으로 update query를 적용하기 위해 영속성 컨텍스트에서 두 개의 객체를 관리 해야 한다.  
(변경 전 객체 내용, 변경 후 객체 내용)

```kotlin
val findMember = memberRepository.findByIdOrNull(member1.id)!!
```
JPA find 하는 순간 영속성 컨텍스트에서 객체를 관리하게 되는 것임  
만약 단순 조회용으로 하고 싶을 때 비효율 발생

```kotlin
// JPA hints
@QueryHints(QueryHint(name = "org.hibernate.readOnly", value = "true")) // readOnly
fun findReadOnlyByUsername(username: String): Member?
```
위와 같이하면 snapshot 기능을 사용하지 않게 되어버려 변경감지 적용되지 않는다.  
단순 조회용으로 사용하고 싶을 때 사용 가능  
하지만 요즘은 성능자체가 좋아져서 큰 성능 개선 효과는 보이지 않는다.

<br>

## :pushpin: 확장 기능

### 사용자 정의 리포지토리 구현

- 기존 JPA Repository에 custom한 method 적용하고 싶을 때
- JDBC 기술 적용(JdbcTemplate) 따로 적용 가능
- JPA entityManager 적용 가능
- **QueryDSL 적용**

```kotlin
interface MemberRepositoryCustom {
  fun findMemberCustom(): List<Member>
}

// MemberRepository
interface MemberRepository: JpaRepository<Member, Long>, MemberRepositoryCustom {
    //...
}
```
Custom Repository interface 생성하고 기존 JPA Repository에 적용

```kotlin
class MemberRepositoryImpl(
    private val em: EntityManager
): MemberRepositoryCustom {
    override fun findMemberCustom(): List<Member> {
        return em.createQuery("select m from Member m", Member::class.java)
            .resultList
    }
}
```
`MemberRepositoryCustom`의 구현체 적용  
여기서 규칙이 기존 JPA Repository 이름에 `Impl`로 네이밍해야 한다. (`MemberRepository` + `Impl`)

- 실무에서 주의점  

화면에 맞춘 복잡한 쿼리들이 존재  
**핵심 비즈니스 로직 쿼리와 화면에 맞춘 복잡한 쿼리에 대해서 repository를 두 부분으로 나눔**  
(ex. 결제시스템에서 핵심 결제 비즈니스 쿼리(JPA)와 통계성 조회 쿼리(어드민, 리포트 등) 두 부분에 대해서 따로 repository 구성)

### Auditing
- 표준 JPA 적용
  - `@PrePersist`, `@PostPersist`, `@PreUpdate`, `@PostUpdate`

```kotlin
@PrePersist
fun prePersist() {
    val now = LocalDateTime.now()
    createdDate = now
    updatedDate = now
}

@PreUpdate
fun preUpdate() {
    updatedDate = LocalDateTime.now()
}
```
- Spring Data JPA 적용

```kotlin
@EnableJpaAuditing
```
Config 파일이나 root application 파일에 auditing 어노테이션 적용  
```kotlin
@CreatedDate
@Column(updatable = false)
lateinit var createdDate: LocalDateTime
    protected set

@LastModifiedDate
lateinit var lastModifiedDate: LocalDateTime
    protected set
```