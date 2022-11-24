package io.beaniejoy.coresecurity.repository

import io.beaniejoy.coresecurity.domain.entity.Resources
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional

@Transactional
interface ResourcesRepository : JpaRepository<Resources, Long> {

    @Query("select r from Resources r join fetch r.roleSet where r.resourceType = 'url' order by r.orderNum desc")
    fun findAllResources(): List<Resources>

//    @EntityGraph(attributePaths = ["roleSet"])
//    override fun findAll(): MutableList<Resources>
}