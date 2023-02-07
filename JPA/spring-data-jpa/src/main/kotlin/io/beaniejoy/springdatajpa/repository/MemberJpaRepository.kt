package io.beaniejoy.springdatajpa.repository

import io.beaniejoy.springdatajpa.entity.Member
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Repository

@Repository
class MemberJpaRepository(
    @PersistenceContext
    private val em: EntityManager
) {
    fun save(member: Member): Member {
        em.persist(member)
        return member
    }

    fun delete(member: Member) {
        em.remove(member)
    }

    fun findAll(): List<Member> {
        return em.createQuery("select m from Member m", Member::class.java).resultList
    }

    fun findById(id: Long): Member? {
        return em.find(Member::class.java, id)
    }

    fun count(): Long {
        return em.createQuery("select count(m) from Member m", Long::class.java).singleResult
    }

    fun find(id: Long): Member? {
        return em.find(Member::class.java, id)
    }
}
