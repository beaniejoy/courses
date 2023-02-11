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