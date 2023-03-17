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