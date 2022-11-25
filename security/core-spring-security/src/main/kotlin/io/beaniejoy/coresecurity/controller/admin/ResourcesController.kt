package io.beaniejoy.coresecurity.controller.admin

import io.beaniejoy.coresecurity.domain.dto.ResourcesDto
import io.beaniejoy.coresecurity.domain.entity.Resources
import io.beaniejoy.coresecurity.domain.entity.Role
import io.beaniejoy.coresecurity.repository.RoleRepository
import io.beaniejoy.coresecurity.security.metadatasource.UrlFilterInvocationSecurityMetadataSource
import io.beaniejoy.coresecurity.service.ResourcesService
import io.beaniejoy.coresecurity.service.RoleService
import mu.KLogging
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping

@Controller
class ResourcesController(
    private val resourcesService: ResourcesService,
    private val roleRepository: RoleRepository,
    private val roleService: RoleService,
    private val filterInvocationSecurityMetadataSource: UrlFilterInvocationSecurityMetadataSource
) {
    companion object : KLogging()

    @GetMapping("/admin/resources")
    fun getResources(model: Model): String? {
        val resources: List<Resources> = resourcesService.getResources()
        model["resources"] = resources
        return "admin/resource/list"
    }

    @PostMapping("/admin/resources")
    fun createResources(resourcesDto: ResourcesDto): String? {
        val role: Role = roleRepository.findByRoleName(resourcesDto.roleName!!)
            ?: throw RuntimeException("Role(${resourcesDto.roleName}) not existed")

        logger.info { "find role ${role.roleName}" }
        val roles = HashSet<Role>().apply {
            this.add(role)
        }

        val resourcesEntity = resourcesDto.id?.let {
            resourcesService.getResources(it).apply {
                this.updateEntity(
                    resourceName = resourcesDto.resourceName!!,
                    httpMethod = resourcesDto.httpMethod!!,
                    orderNum = resourcesDto.orderNum!!,
                    resourceType = resourcesDto.resourceType!!,
                    roleSet = roles
                )
            }
        } ?: Resources.createResources(
            resourceName = resourcesDto.resourceName!!,
            httpMethod = resourcesDto.httpMethod!!,
            orderNum = resourcesDto.orderNum!!,
            resourceType = resourcesDto.resourceType!!,
            roleSet = roles
        )

        resourcesService.createResources(resourcesEntity)

        // 신규 자원이 생성되었을 때 metadataSource 정보 갱신
        filterInvocationSecurityMetadataSource.reload()

        return "redirect:/admin/resources"
    }

    @GetMapping("/admin/resources/register")
    fun viewRoles(model: Model): String? {
        val roleList = roleService.getRoles()
        model["roleList"] = roleList

        val roleSet = HashSet<Role>().apply {
            this.add(Role.empty())
        }

        model["resources"] = ResourcesDto().apply {
            this.roleSet = roleSet
        }

        return "admin/resource/detail"
    }

    @GetMapping("/admin/resources/{id}")
    fun getResources(@PathVariable id: Long, model: Model): String {
        model["roleList"] = roleService.getRoles()

        val resources: Resources = resourcesService.getResources(id)

        model["resources"] = ResourcesDto.of(resources)

        return "admin/resource/detail"
    }

    @GetMapping("/admin/resources/delete/{id}")
    fun removeResources(@PathVariable id: Long, model: Model): String {
        resourcesService.deleteResources(id)
        // 자원이 삭제되었을 때 metadataSource 정보 갱신
        filterInvocationSecurityMetadataSource.reload()

        return "redirect:/admin/resources"
    }
}