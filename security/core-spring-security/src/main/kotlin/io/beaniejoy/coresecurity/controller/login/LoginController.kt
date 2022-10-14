package io.beaniejoy.coresecurity.controller.login

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
class LoginController {
    @GetMapping("/login")
    fun login(): String {
        return "user/login/login"
    }

    @GetMapping("/logout")
    fun logout(request: HttpServletRequest, response: HttpServletResponse): String {
        SecurityContextHolder.getContext().authentication?.run {
            SecurityContextLogoutHandler().logout(request, response, this)
        }

        return "redirect:/login"
    }
}