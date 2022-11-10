package io.beaniejoy.coresecurity.repository

import io.beaniejoy.coresecurity.domain.entity.Role
import org.springframework.data.jpa.repository.JpaRepository

interface RoleRepository : JpaRepository<Role, Long> {
    fun findByRoleName(name: String): Role?
}