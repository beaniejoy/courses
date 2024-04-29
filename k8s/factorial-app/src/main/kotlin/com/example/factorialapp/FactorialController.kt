package com.example.factorialapp

import mu.KLogging
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

@RestController
class FactorialController(
    private val factorialService: FactorialService
) {
    companion object: KLogging()

    @GetMapping("/factorial")
    fun factorial(@RequestParam("n") n: Int): BigDecimal {
        if (n < 0) {
            throw ArithmeticException("n could be natural number")
        }

        logger.info { "factorial : $n" }
        return factorialService.calculate(n)
    }
}