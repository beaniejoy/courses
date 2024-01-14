package io.beaniejoy.cors1

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class Cors1Controller {
    @GetMapping("/")
    fun index(): String {
        return "index"
    }
}