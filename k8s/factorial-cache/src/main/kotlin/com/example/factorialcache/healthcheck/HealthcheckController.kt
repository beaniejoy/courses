package com.example.factorialcache.healthcheck

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthcheckController {
    @GetMapping("/probe/healthcheck")
    fun healthcheck(): String {
        return "OK"
    }
}
