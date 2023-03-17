package io.beaniejoy.querydsl

import com.querydsl.jpa.impl.JPAQueryFactory
import io.beaniejoy.querydsl.entity.Hello
import io.beaniejoy.querydsl.entity.QHello
import jakarta.persistence.EntityManager
import mu.KLogging
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Commit
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
@Commit
class QuerydslApplicationTests {

    @Autowired
    lateinit var em: EntityManager

    companion object: KLogging()

    @Test
    fun contextLoads() {
        val hello = Hello()
        em.persist(hello)

        val query = JPAQueryFactory(em)
//        val qHello = QHello("h")
        val qHello = QHello.hello

        val result: Hello? = query.selectFrom(qHello).fetchOne()

        logger.info { "hello ${hello.id}" }
        assertThat(result).isEqualTo(hello)
        assertThat(result?.id).isEqualTo(hello.id)
    }

}
