package io.beaniejoy.coresecurity.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class HomeController {

    @GetMapping("/")
    fun home(): String {
        return "home"
    }

    @GetMapping("/login")
    fun login(): String {
        return "login"
    }
}