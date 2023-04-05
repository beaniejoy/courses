package io.beaniejoy.querydsl

import com.querydsl.jpa.impl.JPAQueryFactory
import io.beaniejoy.querydsl.entity.Member
import io.beaniejoy.querydsl.entity.QMember.member
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
    // 멀티스레드 환경에서도 동시성 이슈 없이 작동되도록 주입시켜줌
    // @Transactional 걸린 부분에 따라 처리 되도록 설계됨
    @Autowired
    lateinit var em: EntityManager

    lateinit var queryFactory: JPAQueryFactory

    @BeforeEach
    fun before() {
        queryFactory = JPAQueryFactory(em)

        val teamA = Team.createTeam("teamA")
        val teamB = Team.createTeam("teamB")
        em.persist(teamA)
        em.persist(teamB)

        val member1 = Member.createMember("member1", 10).apply { this.changeTeam(teamA) }
        val member2 = Member.createMember("member2", 20).apply { this.changeTeam(teamA) }
        val member3 = Member.createMember("member3", 10).apply { this.changeTeam(teamB) }
        val member4 = Member.createMember("member4", 20).apply { this.changeTeam(teamB) }
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
//        val m = QMember("m1")
//        val m = QMember.member

        // static import 간편히 사용 가능
        val findMember = queryFactory
            .select(member)
            .from(member)
            .where(member.username.eq("member1")) // 파라미터 바인딩
            .fetchOne()

        assertThat(findMember?.username).isEqualTo("member1")
    }

    @Test
    fun search() {
        val member = queryFactory
            .selectFrom(member)
            .where(
                member.username.eq("member1")
                    .and(member.age.eq(10))
            )
            .fetchOne()

        assertThat(member!!.username).isEqualTo("member1")
    }

    // 위 내용과 같음(","는 and) - 김영한님 추천
    // 중간에 null 들어가면 알아서 조건에서 무시
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

    @Test
    fun resultFetch() {
        val list: List<Member> = queryFactory
            .selectFrom(member)
            .fetch()

        val one: Member? = queryFactory
            .selectFrom(member)
            .fetchOne()
    }
}