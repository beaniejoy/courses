package io.beaniejoy.coresecurity.controller.user

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class MessageController {

    @GetMapping("/messages")
    fun message(): String {
        return "user/messages"
    }
}