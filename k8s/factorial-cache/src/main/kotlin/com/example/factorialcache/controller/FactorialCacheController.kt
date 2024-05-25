package com.example.factorialcache.controller

import com.example.factorialcache.exception.IncorrectApiKeyException
import com.example.factorialcache.service.cache.FactorialCacheService
import com.example.factorialcache.service.FactorialCalculateService
import com.example.factorialcache.service.FactorialTaskService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class FactorialCacheController(
    @Value("\${factorial.language}")
    private val language: String,
    @Value("\${factorial.api-key}")
    private val apiKey: String,
    @Qualifier("factorialRedisCacheService")
    private val cacheService: FactorialCacheService,
    private val calculateService: FactorialCalculateService,
    private val taskService: FactorialTaskService
) {

    @GetMapping("/factorial/{n}")
    fun calculateFactorial(
        @PathVariable("n") n: Int,
        @RequestParam(value = "key", required = false) key: String?
    ): String {
        if (n > 10) {
            if (apiKey != key) {
                throw IncorrectApiKeyException("To calculate more than 10 factorials, you need the correct api key")
            }
        }

        val result = cacheService.cachedFactorial(n)
            ?: if (n <= 1000) calculateService.getCalculatedResult(n).also {
                cacheService.cacheFactorial(n, it)
            } else {
                val size = taskService.saveCalculationTask(n)
                return when (language) {
                    "ko" -> "${n}! 계산이 예약되었습니다. 남은 작업 : $size "
                    "en" -> "${n}! has been scheduled. Remain task: $size"
                    else -> "Unsupported language: $language"
                }
            }

        return when (language) {
            "ko" -> "$n 팩토리얼은 $result 입니다."
            "en" -> "the factorial of $n is $result"
            else -> "Unsupported language: $language"
        }
    }
}