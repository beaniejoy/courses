package io.beaniejoy.coresecurity.controller.admin

import io.beaniejoy.coresecurity.domain.dto.AccountDto
import io.beaniejoy.coresecurity.domain.entity.Account
import io.beaniejoy.coresecurity.domain.entity.Role
import io.beaniejoy.coresecurity.service.RoleService
import io.beaniejoy.coresecurity.service.UserService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping

@Controller
class UserManagerController(
    private val userService: UserService,
    private val roleService: RoleService,
) {

    @GetMapping("/admin/accounts")
    fun getUsers(model: Model): String? {
        val accounts: List<Account> = userService.getUsers()
        model["accounts"] = accounts

        return "admin/user/list"
    }

    @PostMapping("/admin/accounts")
    fun modifyUser(accountDto: AccountDto): String {
        userService.modifyUser(accountDto)

        return "redirect:/admin/accounts"
    }

    @GetMapping("/admin/accounts/{id}")
    fun getUser(@PathVariable(value = "id") id: Long, model: Model): String? {
        val accountDto = userService.getUser(id)
        val roleList: List<Role> = roleService.getRoles()

        model["account"] = accountDto
        model["roleList"] = roleList

        return "admin/user/detail"
    }

    @GetMapping("/admin/accounts/delete/{id}")
    fun removeUser(@PathVariable(value = "id") id: Long, model: Model): String? {
        userService.deleteUser(id)

        return "redirect:/admin/users"
    }
}