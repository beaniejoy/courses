package io.beaniejoy.springdatajpa

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
// SpringBoot 에서는 아래 설정을 자동으로 해준다.
//@EnableJpaRepositories(basePackages = ["io.beaniejoy.springdatajpa.repository"])
class SpringDataJpaApplication

fun main(args: Array<String>) {
    runApplication<SpringDataJpaApplication>(*args)
}
