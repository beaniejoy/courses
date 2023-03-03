package io.beaniejoy.springdatajpa.repository

import io.beaniejoy.springdatajpa.dto.MemberDto
import io.beaniejoy.springdatajpa.entity.BaseEntity
import io.beaniejoy.springdatajpa.entity.Member
import io.beaniejoy.springdatajpa.entity.Team
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.IncorrectResultSizeDataAccessException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {
    // spring data jpa 알아서 구현체를 만들어서 injection을 해준다.
    @Autowired
    lateinit var memberRepository: MemberRepository
    @Autowired
    lateinit var teamRepository: TeamRepository
    @Autowired
    lateinit var em: EntityManager

    @Test
    fun testMember() {
        println("memberRepository ${memberRepository.javaClass}") // memberRepository class jdk.proxy2.$Proxy118
        val member = Member.createMember("memberA", 20)
        val savedMember = memberRepository.save(member)

        val findMember = memberRepository.findByIdOrNull(savedMember.id)

        assertThat(findMember!!.id).isEqualTo(member.id)
        assertThat(findMember.username).isEqualTo(member.username)
    }

    @Test
    fun basicCRUD() {
        val member1 = Member.createMember("member1", 10)
        val member2 = Member.createMember("member2", 20)
        memberRepository.save(member1)
        memberRepository.save(member2)

        // 단건 조회 검증
        val findMember1 = memberRepository.findByIdOrNull(member1.id)
        val findMember2 = memberRepository.findByIdOrNull(member2.id)
        assertThat(findMember1).isEqualTo(member1)
        assertThat(findMember2).isEqualTo(member2)

        // 리스트 조회 검증
        val all = memberRepository.findAll()
        assertThat(all.size).isEqualTo(2)

        // count 검증
        val count = memberRepository.count()
        assertThat(count).isEqualTo(2)

        // 삭제 검증
        memberRepository.delete(member1)
        memberRepository.delete(member2)
        val deletedCount = memberRepository.count()
        assertThat(deletedCount).isEqualTo(0)
    }

    @Test
    fun findByUsernameAndAgeGreaterThan() {
        val member1 = Member.createMember("member1", 10)
        val member2 = Member.createMember("member1", 20)
        memberRepository.save(member1)
        memberRepository.save(member2)

        val result = memberRepository.findByUsernameAndAgeGreaterThan("member1", 15)

        assertThat(result[0].username).isEqualTo("member1")
        assertThat(result[0].age).isEqualTo(20)
        assertThat(result.size).isEqualTo(1)
    }

    @Test
    fun findHelloBy() {
        val member1 = Member.createMember("member1", 10)
        val member2 = Member.createMember("member1", 20)
        memberRepository.save(member1)
        memberRepository.save(member2)

        // 전체 조회와 같다.
        val result = memberRepository.findHelloBy()
        assertThat(result.size).isEqualTo(2)
    }

    @Test
    fun testNamedQuery() {
        val member1 = Member.createMember("member1", 10)
        val member2 = Member.createMember("member2", 20)
        memberRepository.save(member1)
        memberRepository.save(member2)

        val result = memberRepository.findByUsername("member1")
        assertThat(result[0].username).isEqualTo("member1")
        assertThat(result[0].age).isEqualTo(10)
        assertThat(result.size).isEqualTo(1)
    }

    @Test
    fun testQuery() {
        val member1 = Member.createMember("member1", 10)
        val member2 = Member.createMember("member2", 20)
        memberRepository.save(member1)
        memberRepository.save(member2)

        val result = memberRepository.findUser("member1", 10)
        assertThat(result[0].username).isEqualTo("member1")
        assertThat(result[0].age).isEqualTo(10)
        assertThat(result.size).isEqualTo(1)
    }

    @Test
    fun findUsernameList() {
        val member1 = Member.createMember("member1", 10)
        val member2 = Member.createMember("member2", 20)
        memberRepository.save(member1)
        memberRepository.save(member2)

        val result = memberRepository.findUsernameList()
        result.forEach {
            println("name: $it")
        }
    }

    @Test
    fun findMemberDto() {
        val team = Team.createTeam("teamA")
        teamRepository.save(team)
        val member1 = Member.createMember("member1", 10).apply {
            this.changeTeam(team)
        }
        memberRepository.save(member1)

        val result = memberRepository.findMemberDto()
        result.forEach {
            println("dto = $it")
        }
    }

    @Test
    fun findByNames() {
        val member1 = Member.createMember("member1", 10)
        val member2 = Member.createMember("member2", 20)
        memberRepository.save(member1)
        memberRepository.save(member2)

        val result = memberRepository.findByNames(listOf("member1", "member2"))
        result.forEach {
            println("name: $it")
        }
    }

    @Test
    fun returnType() {
        val member1 = Member.createMember("member1", 10)
        val member2 = Member.createMember("member2", 20)
        val member3 = Member.createMember("member2", 30)
        memberRepository.save(member1)
        memberRepository.save(member2)
        memberRepository.save(member3)

        val result1 = memberRepository.findListByUsername("member1")
        val resultNone = memberRepository.findListByUsername("none")
        assertThat(resultNone.size).isEqualTo(0) // 없으면 Empty List를 반환

        val result2 = memberRepository.findMemberByUsername("member1")
        val result3 = memberRepository.findOptionalByUsername("member1")

        // NonUniqueResultException > JPA exception
        // IncorrectResultSizeDataAccessException > springframework exception으로 변환
        assertThrows<IncorrectResultSizeDataAccessException> {
            memberRepository.findMemberByUsername("member2")
        }
    }

    @Test
    fun testPaging() {
        memberRepository.save(Member.createMember("member1", 10))
        memberRepository.save(Member.createMember("member2", 10))
        memberRepository.save(Member.createMember("member3", 10))
        memberRepository.save(Member.createMember("member4", 10))
        memberRepository.save(Member.createMember("member5", 10))

        val age = 10

        val pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"))

        val members = memberRepository.findByAge(age, pageRequest)
        assertThat(members.content.size).isEqualTo(3)
        assertThat(members.totalElements).isEqualTo(5)
        assertThat(members.number).isEqualTo(0)
        assertThat(members.totalPages).isEqualTo(2)
        assertThat(members.isFirst).isTrue
        assertThat(members.hasNext()).isTrue

        members.content.forEach {
            println("member = ${it.id}, ${it.username}")
        }

        // Page 내의 dto로 변환가능
        val memberDtos: Page<MemberDto> = members.map { MemberDto(it.id, it.username, it.team?.name) }

        // Page > Slice(count 쿼리 X)
        // size를 3개로 했는데 slice로 하면 +1해서 4가 날라감 (더보기 기능에 대해서 사용)
        val membersSlice = memberRepository.findWithSliceByAge(age, pageRequest)
        assertThat(membersSlice.content.size).isEqualTo(3)
        assertThat(membersSlice.number).isEqualTo(0)
        assertThat(membersSlice.isFirst).isTrue
        assertThat(membersSlice.hasNext()).isTrue

        // limit 0, 3은 적용 하되 딱 3개만 뽑고 싶은 경우(paging 기능 상관 없이)
        val membersList = memberRepository.findWithListByAge(age, pageRequest)
        assertThat(membersList.size).isEqualTo(3)

        // countQuery 사용(원장 쿼리와 count 쿼리를 분리해야 하는 경우 - join 있을 때)
        memberRepository.findWithCountQueryByAge(age, pageRequest)
    }

    @Test
    fun bulkUpdate() {
        // given
        memberRepository.save(Member.createMember("member1", 10))
        memberRepository.save(Member.createMember("member2", 15))
        memberRepository.save(Member.createMember("member3", 21))
        memberRepository.save(Member.createMember("member4", 28))
        memberRepository.save(Member.createMember("member5", 31))

        println("========== save ============")
        // when
        val resultCount = memberRepository.bulkAgePlus(20)
//        em.flush()
//        em.clear()
        println("========== bulkUpdate ============")

        val result = memberRepository.findByUsername("member5")

        println("========== findByUsername ============")
        val member5 = result[0]
        println("member5 = ${member5.age}") // 31로 update 처리 안됨

        // then
        assertThat(resultCount).isEqualTo(3)
    }

    @Test
    fun findMemberLazy() {
        // given
        // member1 > teamA
        // member2 > teamB

        val teamA = Team.createTeam("teamA")
        val teamB = Team.createTeam("teamB")
        teamRepository.save(teamA)
        teamRepository.save(teamB)

        val member1 = Member.createMember("member1", 10).apply {
            this.changeTeam(teamA)
        }
        val member2 = Member.createMember("member2", 10).apply {
            this.changeTeam(teamB)
        }
        memberRepository.save(member1)
        memberRepository.save(member2)

        em.flush()
        em.clear()

        // N+1 문제 발생 (fetch lazy mode) > member.team.name 할 때 team 조회 쿼리 발생
        // m.team > Team$HibernateProxy$... (proxy)
//        val members = memberRepository.findAll()

        // fetch join으로 N+1 해결
        // m.team > springdatajpa.entity.Team
//        val members = memberRepository.findMemberFetchJoin()

        // EntityGraph 적용된 findAll
        val members = memberRepository.findEntityGraphByUsername("member1")

        members.forEach {
            println("member = ${it.username}")
            println("member.teamClass = ${it.team!!.javaClass}") //
            println("member.team = ${it.team!!.name}")
        }
    }

    @Test
    fun queryHint() {
        // given
        val member1 = Member.createMember("member1", 10)
        memberRepository.save(member1)

        em.flush()
        em.clear()

        // when
//        val findMember = memberRepository.findByIdOrNull(member1.id)!!
        val findMember = memberRepository.findReadOnlyByUsername("member1")!! // readOnly
        findMember.apply {
            this.updateName("update member1")
        }

        // update query (dirty checking)
        // readOnly hint 적용시 변경감지 체크 X (조회만을 위한 효율 극대화, snapshot 관리 X)
        em.flush()
    }

    @Test
    fun lock() {
        // given
        val member1 = Member.createMember("member1", 10)
        memberRepository.save(member1)

        em.flush()
        em.clear()

        // when
        val result = memberRepository.findLockByUsername("member1") // readOnly
    }

    @Test
    fun callCustom() {
        // custom repository method 사용
        // MemberRepository > MemberRepositoryCustom (MemberRepositoryImpl 구현체 사용)
        memberRepository.findMemberCustom()
    }

    @Test
    fun jpaEventBaseEntity() {
        // given
        val member = Member.createMember("member1", 10)
        memberRepository.save(member) // @PrePersist

        Thread.sleep(1000)
        member.updateName("update member")

        em.flush() // @PreUpdate
        em.clear()

        // when
        val findMember = memberRepository.findByIdOrNull(member.id)!!

        // then
        println("findMember.createdDate = ${findMember.createdDate}")
        println("findMember.updatedDate = ${findMember.lastModifiedDate}")
        println("findMember.createdBy = ${findMember.createdBy}")
        println("findMember.updatedBy = ${findMember.updatedBy}")
    }
}