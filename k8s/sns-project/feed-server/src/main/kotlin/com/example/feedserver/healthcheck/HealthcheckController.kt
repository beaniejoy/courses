package com.example.feedserver.healthcheck

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/healthcheck")
class HealthcheckController {
    @GetMapping("/ready")
    fun readinessProbe(): String {
        return "ready"
    }

    @GetMapping("/live")
    fun livenessProbe(): String {
        return "ok"
    }
}