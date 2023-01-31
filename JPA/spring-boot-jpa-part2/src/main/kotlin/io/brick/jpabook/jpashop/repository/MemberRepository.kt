package io.brick.jpabook.jpashop.repository

import io.brick.jpabook.jpashop.domain.Member
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface MemberRepository: JpaRepository<Member, Long> {
    fun findByName(name: String): List<Member>

    @Query("select m from Member m where m.id = :memberId")
    fun findOneWithJpql(memberId: Long): Member?
}