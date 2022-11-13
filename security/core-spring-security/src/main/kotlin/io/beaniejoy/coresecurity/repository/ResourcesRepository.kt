package io.beaniejoy.coresecurity.repository

import io.beaniejoy.coresecurity.domain.entity.Resources
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ResourcesRepository : JpaRepository<Resources, Long> {

    @Query("select r from Resources r join fetch r.roleSet where r.resourceType = 'url' order by r.orderNum desc")
    fun findAllResources(): List<Resources>
}