# QueryDSL

## Setting

```kotlin
plugins {
    //...
    kotlin("kapt") version "1.7.22"
}

dependencies {
    // querydsl
    implementation("com.querydsl:querydsl-jpa:$querydslVersion:jakarta")
    kapt("com.querydsl:querydsl-apt:$querydslVersion:jakarta")
}
```
- Spring Boot 3.x 버전으로 진입하면서 `javax` > `jarkarta` 변경
- querydsl-kapt: Code Generation 용도(QEntity 클래스 파일 생성) 
- querydsl-jpa: 실제 애플리케이션 코드 작성할 때 필요한 라이브러리

```
implementation("com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.9.0")
```
- 1.9.0 버전에서 Spring Boot 3.x 버전 대응

## :pushpin: 기본 문법

### 기본 QType 활용

```kotlin
@Test
fun startQuerydsl() {
//        val m = QMember("m")
//        val m = QMember.member

    // static import 간편히 사용 가능
    val findMember = queryFactory
        .select(member)
        .from(member)
        .where(member.username.eq("member1")) // 파라미터 바인딩
        .fetchOne()

    assertThat(findMember?.username).isEqualTo("member1")
}
```
- `QMember("m1")`으로 table에 alias 부여가능(같은 테이블 조인할 때만 사용함) 
- **static import를 사용해서 QType member 사용하는 것을 추천** 

### 검색 조건 쿼리 

```kotlin
// 위 내용과 같음(","는 and) - 김영한님 추천
// 조건 중간 null 들어가면 알아서 조건에서 무시
@Test
fun searchAndParam() {
    val member = queryFactory
        .selectFrom(member)
        .where(
            member.username.eq("member1"),
            member.age.eq(10)
        )
        .fetchOne()

    assertThat(member!!.username).isEqualTo("member1")
}
```
