package io.beaniejoy.springdatajpa.repository

import io.beaniejoy.springdatajpa.entity.Member
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
class MemberJpaRepositoryTest {
    @Autowired
    lateinit var memberJpaRepository: MemberJpaRepository

    @Test
    @Transactional
    fun testMember() {
        val member = Member.createMember("memberA", 20)
        val savedMember = memberJpaRepository.save(member)

        // 1차 캐시로 select 쿼리 X -> PC에서 조회
        val findMember = memberJpaRepository.find(savedMember.id)

        assertThat(findMember!!.id).isEqualTo(member.id)
        assertThat(findMember.username).isEqualTo(member.username)
    }
}