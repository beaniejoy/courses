package io.beaniejoy.springdatajpa.repository

import io.beaniejoy.springdatajpa.entity.Team
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Repository

@Repository
class TeamJpaRepository(
    @PersistenceContext
    private val em: EntityManager
) {
    fun save(team: Team): Team {
        em.persist(team)
        return team
    }

    fun delete(team: Team) {
        em.remove(team)
    }

    fun findAll(): List<Team> {
        return em.createQuery("select t from Team t", Team::class.java).resultList
    }

    fun findById(id: Long): Team? {
        return em.find(Team::class.java, id)
    }

    fun count(): Long {
        return em.createQuery("select count(t) from Team t", Long::class.java).singleResult
    }
}