package io.beaniejoy.coresecurity.controller.admin

import io.beaniejoy.coresecurity.domain.dto.ResourcesDto
import io.beaniejoy.coresecurity.domain.entity.Resources
import io.beaniejoy.coresecurity.domain.entity.Role
import io.beaniejoy.coresecurity.repository.RoleRepository
import io.beaniejoy.coresecurity.service.ResourcesService
import io.beaniejoy.coresecurity.service.RoleService
import io.beaniejoy.coresecurity.service.SecurityResourceService
import org.springframework.security.access.ConfigAttribute
import org.springframework.security.web.util.matcher.RequestMatcher
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class ResourcesController(
    private val resourcesService: ResourcesService,
    private val roleRepository: RoleRepository,
    private val roleService: RoleService,
    private val securityResourcesService: SecurityResourceService
) {
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

        val roles = HashSet<Role>().apply {
            this.add(role)
        }

        resourcesService.createResources(Resources.createResources(
            resourceName = resourcesDto.resourceName!!,
            httpMethod = resourcesDto.httpMethod!!,
            orderNum = resourcesDto.orderNum!!,
            resourceType = resourcesDto.resourceType!!,
            roleSet = roles
        ))

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

        return "redirect:/admin/resources"
    }
}