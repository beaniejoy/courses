package io.beaniejoy.querydsl

import com.querydsl.jpa.impl.JPAQueryFactory
import io.beaniejoy.querydsl.entity.Member
import io.beaniejoy.querydsl.entity.QMember
import io.beaniejoy.querydsl.entity.Team
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@Transactional
class QuerydslBasicTest {
    @Autowired
    lateinit var em: EntityManager

    @BeforeEach
    fun before() {
        val teamA = Team.createTeam("teamA")
        val teamB = Team.createTeam("teamB")
        em.persist(teamA)
        em.persist(teamB)

        val member1 = Member.createMember("member1", 10).apply { this.changeTeam(teamA) }
        val member2 = Member.createMember("member2", 10).apply { this.changeTeam(teamA) }
        val member3 = Member.createMember("member3", 10).apply { this.changeTeam(teamB) }
        val member4 = Member.createMember("member4", 10).apply { this.changeTeam(teamB) }
        em.persist(member1)
        em.persist(member2)
        em.persist(member3)
        em.persist(member4)

        em.flush()
        em.clear()
    }

    @Test
    fun startJPQL() {
        // find member1
        val findMember = em.createQuery("select m from Member m where m.username = :username", Member::class.java)
            .setParameter("username", "member1")
            .singleResult

        assertThat(findMember.username).isEqualTo("member1")
    }

    @Test
    fun startQuerydsl() {
        val queryFactory = JPAQueryFactory(em)
        val m = QMember("m")

        val findMember = queryFactory
            .select(m)
            .from(m)
            .where(m.username.eq("member1"))
            .fetchOne()

        assertThat(findMember?.username).isEqualTo("member1")
    }
}