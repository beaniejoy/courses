package io.beaniejoy.coresecurity.domain.dto

import io.beaniejoy.coresecurity.domain.entity.Role

data class RoleDto(
    val id: Long? = null,
    val roleName: String? = null,
    val roleDesc: String? = null,
) {
    companion object {
        fun of(role: Role): RoleDto {
            return RoleDto(
                id = role.id,
                roleName = role.roleName,
                roleDesc = role.roleDesc
            )
        }
    }
}