package io.beaniejoy.springdatajpa.repository

import io.beaniejoy.springdatajpa.entity.Member
import jakarta.persistence.EntityManager

class MemberRepositoryImpl(
    private val em: EntityManager
): MemberRepositoryCustom {
    override fun findMemberCustom(): List<Member> {
        return em.createQuery("select m from Member m", Member::class.java)
            .resultList
    }
}