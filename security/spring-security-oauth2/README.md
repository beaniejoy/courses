# Spring Security OAuth2.0

- [source code github link](https://github.com/onjsdnjs/spring-security-oauth2)

<br>

## Spring Boot 3.0 이슈

[Custom DSL 적용시 주의점](https://docs.spring.io/spring-security/reference/servlet/configuration/java.html#jc-custom-dsls)

<br>

## 모듈 구성

1. Spring Security Fundamentals
   - spring-security-basic 
   - cors-1 (client)
   - cors-2 (server - cors config)

2. OAuth 2.0

<br>

## OAuth 2.0 필기 내용

- [keycloak 다운](https://www.keycloak.org/downloads)
  - 인가서버의 역할을 할 수 있도록 하는 오픈소스

```shell
./kc.sh start-dev
```
dev 모드로 실행  
keycloak 로컬 admin 계정 > ID: admin / PW: beaniejoy
keycloak user > PW: 1234

<br>

## OAuth 기본 이해하기

```shell
GET http://localhost:8080/realms/oauth2/protocol/openid-connect/auth?response_type=code&client_id=oauth2-client-app&scope=profile email&redirect_url=http://localhost:8081
```
keycloak에 access token 요청하기 전에 1단계 과정으로 임시코드 발급받는 api  
(임시코드로 access token과 교환하게 된다.)