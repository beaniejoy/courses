package io.beaniejoy.springdatajpa.entity

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
@Rollback(false)
class MemberTest {

    @PersistenceContext
    lateinit var em: EntityManager

    @Test
    fun testEntity() {
        val teamA = Team.createTeam("teamA")
        val teamB = Team.createTeam("teamB")
        em.persist(teamA)
        em.persist(teamB)

        val member1 = Member.createMember("member1", 10).apply { this.changeTeam(teamA) }
        val member2 = Member.createMember("member2", 20).apply { this.changeTeam(teamA) }
        val member3 = Member.createMember("member3", 30).apply { this.changeTeam(teamB) }
        val member4 = Member.createMember("member4", 40).apply { this.changeTeam(teamB) }
        em.persist(member1)
        em.persist(member2)
        em.persist(member3)
        em.persist(member4)

        // 초기화
        em.flush()
        em.clear()

        val members = em.createQuery("select m from Member m", Member::class.java).resultList

        members.forEach {
            println("member=${it.username}, ${it.age}")
            println(">> member.team=${it.team?.name}")
        }
    }
}