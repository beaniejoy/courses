# Spring Batch Basic

- 강의에 대한 필기내용

## Spec
- Java 17

## Setup
```java
@SpringBootApplication
@EnableBatchProcessing
public class SpringBatchExampleApplication {
    //...
}
```

## 필기내용

- mysql bulk insert 활성화
```yaml
jdbc-url: jdbc:mysql://localhost:3306/spring_batch?characterEncoding=UTF-8&serverTimezone=UTC&rewriteBatchedStatements=true
```