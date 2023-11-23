package io.beaniejoy.querydsl

import com.querydsl.core.Tuple
import com.querydsl.jpa.impl.JPAQueryFactory
import io.beaniejoy.querydsl.entity.Member
import io.beaniejoy.querydsl.entity.QMember.member
import io.beaniejoy.querydsl.entity.QTeam.team
import io.beaniejoy.querydsl.entity.Team
import jakarta.persistence.EntityManager
import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.PersistenceUnit
import jakarta.transaction.Transactional
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Commit

@SpringBootTest
@Transactional
class QuerydslBasicTest {
    // 멀티스레드 환경에서도 동시성 이슈 없이 작동되도록 주입시켜줌
    // @Transactional 걸린 부분에 따라 처리 되도록 설계됨
    @Autowired
    lateinit var em: EntityManager

    lateinit var queryFactory: JPAQueryFactory

    // @Commit이 없으면 기본적으로 Test에 Transactional 있으면 Rollback 시킨다.
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

        assertThat(findMember!!.username).isEqualTo("member1")
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
//        val list: List<Member> = queryFactory
//            .selectFrom(member)
//            .fetch()
//
//        val one: Member? = queryFactory
//            .selectFrom(member)
//            .fetchOne()
//
//        val fetchFirst = queryFactory
//            .selectFrom(member)
//            .fetchFirst() // .limit(1).fetchOne()
//
//        queryFactory
//            .selectFrom(member)
//            .fetchResults() // deprecated
//
//        queryFactory
//            .selectFrom(member)
//            .fetchCount() // deprecated

        val fetchPaging = queryFactory
            .selectFrom(member)
            .offset(0)
            .limit(10)
            .fetch()

    }

    /**
     * 회원 정렬 순서
     * 1. 회원 나이 내림차순 (desc)
     * 2. 회원 이름 오름차순 (asc)
     * 2번에서 이름이 없을시 맨 마지막에 출
     */
    @Test
    fun sort() {
        em.persist(Member.createMember(null, 100))
        em.persist(Member.createMember("member5", 101))
        em.persist(Member.createMember("member6", 101))
        em.persist(Member.createMember("member7", 100))

        val result = queryFactory
            .selectFrom(member)
            .where(member.age.goe(100))
            .orderBy(member.age.desc(), member.username.asc().nullsLast())
            .fetch()

        result.forEach {
            println("Member: $it")
        }
    }

    @Test
    fun paging() {
        // paging
        val result = queryFactory
            .selectFrom(member)
            .orderBy(member.username.desc())
            .offset(1)
            .limit(2)
            .fetch()

        // count query
        val count = queryFactory
            .select(member.count())
            .from(member)
            .fetchOne()

        assertThat(result.size).isEqualTo(2)
        assertThat(count).isEqualTo(4)
    }

    @Test
    fun aggregation() {
        val result: List<Tuple> = queryFactory
            .select(
                member.count(),
                member.age.sum(),
                member.age.avg(),
                member.age.max(),
                member.age.min(),
            )
            .from(member)
            .fetch()

        // Querydsl의 Tuple은 많이 사용 X
        val tuple = result[0]
        assertThat(tuple.get(member.count())).isEqualTo(4)
        assertThat(tuple.get(member.age.sum())).isEqualTo(60)
        assertThat(tuple.get(member.age.avg())).isEqualTo(15.0) // double형
        assertThat(tuple.get(member.age.max())).isEqualTo(20)
        assertThat(tuple.get(member.age.min())).isEqualTo(10)
    }

    /**
     * 팀의 이름과 각 팀의 평균 연령을 구해보자
     */
    @Test
    fun group() {
        val result = queryFactory
            .select(team.name, member.age.avg())
            .from(member)
            .join(member.team, team)
            .groupBy(team.name)
            .fetch()

        val teamA = result[0]
        val teamB = result[1]

        assertThat(teamA.get(team.name)).isEqualTo("teamA")
        assertThat(teamA.get(member.age.avg())).isEqualTo(15.0)
        assertThat(teamB.get(team.name)).isEqualTo("teamB")
        assertThat(teamB.get(member.age.avg())).isEqualTo(15.0)
    }

    @Test
    fun join() {
        // join, innerJoin, rightJoin 다 가능
        val result = queryFactory
            .selectFrom(member)
            .join(member.team, team)
            .where(team.name.eq("teamA"))
            .fetch()

        assertThat(result)
            .extracting("username")
            .containsExactly("member1", "member2")
    }

    @Test
    fun thetaJoin() {
        em.persist(Member.createMember("teamA", 40))
        em.persist(Member.createMember("teamB", 40))
        em.persist(Member.createMember("teamC", 40))

        // cartesian join (연관관계가 없는 두 테이블 모두 가져와서 조인함)
        // join on 절 이용하면 외부 조인도 가능(최신 하이버네이트 버전부터)
        val result = queryFactory
            .select(member)
            .from(member, team)
            .where(member.username.eq(team.name))
            .fetch()

        assertThat(result)
            .extracting("username")
            .containsExactly("teamA", "teamB")
    }

    /**
     * 회원과 팀을 조인하면서, 팀 이름이 teamA인 팀만 조인, 회원은 모두  조회
     * JPQL: SELECT m, t FROM MEMBER m LEFT JOIN m.team ON t.name = 'teamA'
     */
    @Test
    fun join_on_filtering() {
        val result = queryFactory
            .select(member, team)
            .from(member)
            .leftJoin(member.team, team).on(team.name.eq("teamA"))
//            .where(team.name.eq("teamA")) // where 절에서 조건거는 것과 똑같다.
            .fetch()

        for (tuple in result) {
            println("tuple = $tuple")
        }
    }

    /**
     * 연관관계가 없는 엔티티 외부 조인
     * 회원의 이름이 팀 이름과 같은 대상 외부 조인
     * JPQL: SELECT m, t FROM MEMBER m JOIN m.team ON m.username = t.name
     */
    @Test
    fun join_on_no_relation() {
        em.persist(Member.createMember("teamA", 40))
        em.persist(Member.createMember("teamB", 40))
        em.persist(Member.createMember("teamC", 40))

        val result = queryFactory
            .select(member, team)
            .from(member)
            .join(team).on(member.username.eq(team.name))
            .fetch()

        for (tuple in result) {
            println("tuple = $tuple")
        }
    }

    @PersistenceUnit
    lateinit var emf: EntityManagerFactory

    @Test
    fun fetch_join_no() {
        val findMember = queryFactory
            .selectFrom(member)
            .where(member.username.eq("member1"))
            .fetchOne()

        val loaded = emf.persistenceUnitUtil.isLoaded(findMember?.team)
        assertThat(loaded).`as`("페치 조인 미적용").isFalse()
    }

    /**
     * 페치 조인 적용
     * JPQL: SELECT m from Member m INNER JOIN FETCH m.team where m.username = 'member1'
     */
    @Test
    fun fetch_join_use() {
        val findMember = queryFactory
            .selectFrom(member)
            .join(member.team, team).fetchJoin()
            .where(member.username.eq("member1"))
            .fetchOne()

        println(findMember?.team)
        val loaded = emf.persistenceUnitUtil.isLoaded(findMember?.team)
        assertThat(loaded).`as`("페치 조인 적용").isTrue()
    }
}