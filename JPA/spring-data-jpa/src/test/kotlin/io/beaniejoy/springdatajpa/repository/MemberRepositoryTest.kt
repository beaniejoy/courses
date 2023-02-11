package io.beaniejoy.springdatajpa.repository

import io.beaniejoy.springdatajpa.entity.Member
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
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
}