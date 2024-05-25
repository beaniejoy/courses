package com.example.factorialapp

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class FactorialService(
    private val redisTemplate: StringRedisTemplate
) {

    fun calculate(n: Int): BigDecimal {
        if (n <= 1) {
            var elapsedTime: Long;
            val startTime = System.currentTimeMillis()
            do {
                elapsedTime = System.currentTimeMillis() - startTime
            } while (elapsedTime < 500L)
            return BigDecimal.ONE
        }

        return BigDecimal(n).multiply(calculate(n - 1))
    }

//    @Scheduled(fixedDelay = 1000L)
//    fun calculateTask() {
//        if (redisTemplate.hasKey("factorial:task-queue")) {
//            redisTemplate.opsForSet().pop("factorial:task-queue")?.also {
//                val result = calculate(it.toInt())
//
//                redisTemplate.opsForHash<String, String>()
//                    .put("factorial:result-set", it, result.toPlainString())
//            }
//        }
//    }
}
