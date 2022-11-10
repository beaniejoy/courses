package io.beaniejoy.coresecurity.domain.dto

import io.beaniejoy.coresecurity.domain.entity.Resources
import io.beaniejoy.coresecurity.domain.entity.Role

data class ResourcesDto(
    val id: Long? = null,
    val resourceName: String? = null,
    val httpMethod: String? = null,
    val orderNum: Int? = null,
    val resourceType: String? = null,
    val roleName: String? = null,
    var roleSet: Set<Role> = HashSet(),
) {
    companion object {
        fun of(resources: Resources): ResourcesDto {
            return ResourcesDto(
                id = resources.id,
                resourceName = resources.resourceName,
                httpMethod = resources.httpMethod,
                orderNum = resources.orderNum,
                resourceType = resources.resourceType,
                roleSet = resources.roleSet
            )
        }
    }
}