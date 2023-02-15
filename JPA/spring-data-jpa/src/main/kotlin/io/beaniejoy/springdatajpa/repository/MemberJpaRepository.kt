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

    fun findByUsernameAndAgeGreaterThan(username: String, age: Int): List<Member> {
        return em.createQuery("select m from Member m where m.username = :username and m.age > :age", Member::class.java)
            .setParameter("username", username)
            .setParameter("age", age)
            .resultList
    }

    fun findByUsername(username: String): List<Member> {
        return em.createNamedQuery("Member.findByUsername", Member::class.java)
            .setParameter("username", username)
            .resultList
    }

    // paging 처리
    fun findByAge(age: Int, offset: Int, limit: Int): List<Member> {
        return em.createQuery("select m from Member m where m.age = :age order by m.username desc", Member::class.java)
            .setParameter("age", age)
            .setFirstResult(offset)
            .setMaxResults(limit)
            .resultList
    }

    fun totalCount(age: Int): Long {
        return em.createQuery("select count(m) from Member m where m.age = :age", Long::class.java)
            .setParameter("age", age)
            .singleResult
    }

    fun bulkAgePlus(age: Int): Int {
        return em.createQuery("update Member m set m.age = m.age + 1 where m.age >= :age")
            .setParameter("age", age)
            .executeUpdate()
    }
}
