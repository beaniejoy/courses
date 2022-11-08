package io.beaniejoy.coresecurity.controller.login

import io.beaniejoy.coresecurity.domain.Account
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
class LoginController {
    @GetMapping(value = ["/login", "/api/login"])
    fun login(
        @RequestParam(name = "error", required = false) error: String?,
        @RequestParam(name = "exception", required = false) exception: String?,
        model: Model
    ): String {
        model.addAttribute("error", error)
        model.addAttribute("exception", exception)

        return "login"
    }

    @GetMapping("/logout")
    fun logout(request: HttpServletRequest, response: HttpServletResponse): String {
        SecurityContextHolder.getContext().authentication?.run {
            SecurityContextLogoutHandler().logout(request, response, this)
        }

        return "redirect:/login"
    }

    @GetMapping(value =["/denied", "/api/denied"])
    fun accessDenied(
        @RequestParam(name = "exception", required = false) exception: String?,
        model: Model,
    ): String {
        val authentication = SecurityContextHolder.getContext().authentication
        val account = authentication.principal as Account
        model.addAttribute("username", account.username)
        model.addAttribute("exception", exception)

        return "user/login/denied"
    }
}