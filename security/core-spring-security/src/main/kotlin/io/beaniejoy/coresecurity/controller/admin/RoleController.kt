package io.beaniejoy.coresecurity.controller.admin

import io.beaniejoy.coresecurity.domain.dto.RoleDto
import io.beaniejoy.coresecurity.domain.entity.Role
import io.beaniejoy.coresecurity.service.RoleService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping

@Controller
class RoleController(
    private val roleService: RoleService,
) {
    @GetMapping("/admin/roles")
    fun getRoles(model: Model): String {
        val roles: List<Role> = roleService.getRoles()
        model["roles"] = roles

        return "admin/role/list"
    }

    @GetMapping("/admin/roles/register")
    @Throws(Exception::class)
    fun viewRoles(model: Model): String {
        val role = RoleDto()
        model["role"] =  role

        return "admin/role/detail"
    }

    @PostMapping("/admin/roles")
    fun createRole(roleDto: RoleDto): String {
        roleService.createRole(Role.createRole(
            roleName = roleDto.roleName!!,
            roleDesc = roleDto.roleDesc!!
        ))

        return "redirect:/admin/roles"
    }

    @GetMapping("/admin/roles/{id}")
    fun getRole(@PathVariable id: Long, model: Model): String {
        val role = roleService.getRole(id)
        model["role"] = RoleDto.of(role)

        return "admin/role/detail"
    }

    @GetMapping("/admin/roles/delete/{id}")
    fun removeResources(@PathVariable id: Long, model: Model): String {
        roleService.deleteRole(id)

        return "redirect:/admin/resources"
    }
}