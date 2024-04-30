package com.example.factorialapp

import mu.KLogging
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/probe")
class HealthcheckController {
    companion object: KLogging()

    @GetMapping("/startup")
    fun startupCheck(): String {
        logger.info { "startup probe check" }
        return "Good to go"
    }

    @GetMapping("/ready")
    fun readinessCheck(): String {
        logger.info { "readiness probe check" }
        return "Ready"
    }

    @GetMapping("/live")
    fun livenessCheck(): String {
        logger.info { "liveness probe check" }
        return "OK"
    }
}