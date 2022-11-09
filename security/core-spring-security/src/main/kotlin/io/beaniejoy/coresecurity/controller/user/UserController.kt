package io.beaniejoy.coresecurity.controller.user

import io.beaniejoy.coresecurity.domain.entity.Account
import io.beaniejoy.coresecurity.domain.dto.AccountDto
import io.beaniejoy.coresecurity.service.UserService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping

@Controller
class UserController(
    private val userService: UserService,
    private val passwordEncoder: PasswordEncoder
) {

    @GetMapping("/mypage")
    fun myPage(): String {
        return "user/mypage"
    }

    @GetMapping("/users")
    fun createUser(): String {
        return "user/login/register"
    }

    @PostMapping("/users")
    fun createUser(accountDto: AccountDto): String {
        val account = Account.createAccount(
            username = accountDto.username,
            password = passwordEncoder.encode(accountDto.password),
            email = accountDto.email!!,
            age = accountDto.age!!,
            role = accountDto.role!!
        )

        userService.createUser(account)

        return "redirect:/"
    }
}