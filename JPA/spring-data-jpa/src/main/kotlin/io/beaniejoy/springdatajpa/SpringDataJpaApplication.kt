package io.beaniejoy.springdatajpa

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import java.util.*

@EnableJpaAuditing
@SpringBootApplication
// SpringBoot 에서는 아래 설정을 자동으로 해준다.
//@EnableJpaRepositories(basePackages = ["io.beaniejoy.springdatajpa.repository"])
class SpringDataJpaApplication {
    @Bean
    fun auditorProvider(): AuditorAware<String> {
        return AuditorAware<String> { Optional.of(UUID.randomUUID().toString()) }
    }
}

fun main(args: Array<String>) {
    runApplication<SpringDataJpaApplication>(*args)
}
