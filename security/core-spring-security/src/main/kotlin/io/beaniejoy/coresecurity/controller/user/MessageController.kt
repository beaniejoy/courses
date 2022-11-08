package io.beaniejoy.coresecurity.controller.user

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class MessageController {

    @GetMapping("/messages")
    fun message(): String {
        return "user/messages"
    }

    @ResponseBody
    @PostMapping("/api/messages")
    fun apiMessage(): ResponseEntity<String> {
        return ResponseEntity.ok().body("ok");
    }
}