package io.beaniejoy.springdatajpa.repository

import io.beaniejoy.springdatajpa.entity.Member
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
@Rollback(false)
class MemberJpaRepositoryTest {
    @Autowired
    lateinit var memberJpaRepository: MemberJpaRepository

    @Test
    fun testMember() {
        val member = Member.createMember("memberA", 20)
        val savedMember = memberJpaRepository.save(member)

        // 1차 캐시로 select 쿼리 X -> PC에서 조회
        val findMember = memberJpaRepository.find(savedMember.id)

        println("### member: $member")
        println("### savedMember: $savedMember")
        println("### findMember: $findMember")

        assertThat(findMember!!.id).isEqualTo(member.id)
        assertThat(findMember.username).isEqualTo(member.username)
        assertThat(findMember).isEqualTo(member)
    }

    @Test
    fun basicCRUD() {
        val member1 = Member.createMember("member1", 10)
        val member2 = Member.createMember("member2", 20)
        memberJpaRepository.save(member1)
        memberJpaRepository.save(member2)

        // 단건 조회 검증
        val findMember1 = memberJpaRepository.findById(member1.id)
        val findMember2 = memberJpaRepository.findById(member2.id)
        assertThat(findMember1).isEqualTo(member1)
        assertThat(findMember2).isEqualTo(member2)

        // 리스트 조회 검증
        val all = memberJpaRepository.findAll()
        assertThat(all.size).isEqualTo(2)

        // count 검증
        val count = memberJpaRepository.count()
        assertThat(count).isEqualTo(2)

        // 삭제 검증
        memberJpaRepository.delete(member1)
        memberJpaRepository.delete(member2)
        val deletedCount = memberJpaRepository.count()
        assertThat(deletedCount).isEqualTo(0)
    }
}