package com.example.factorialapp

import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class FactorialService {

    fun calculate(n: Int): BigDecimal {
        if (n <= 1) {
            return BigDecimal.ONE
        }

        return BigDecimal(n).multiply(calculate(n - 1))
    }
}
