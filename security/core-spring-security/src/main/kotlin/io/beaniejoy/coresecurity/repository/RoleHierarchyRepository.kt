package io.beaniejoy.coresecurity.repository

import io.beaniejoy.coresecurity.domain.entity.RoleHierarchy
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository

interface RoleHierarchyRepository : JpaRepository<RoleHierarchy, Long> {
    fun findByChildName(roleName: String): RoleHierarchy?

    @EntityGraph(attributePaths = ["parentName"])
    override fun findAll(): MutableList<RoleHierarchy>
}