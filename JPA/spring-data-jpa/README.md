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

