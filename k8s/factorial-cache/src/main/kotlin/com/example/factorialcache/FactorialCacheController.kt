package com.example.factorialcache

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
    private val cacheService: FactorialCacheService,
    private val calculateService: FactorialCalculateService
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
            ?: calculateService.getCalculatedResult(n).also {
                cacheService.cacheFactorial(n, it)
            }

        return when (language) {
            "ko" -> "$n 팩토리얼은 $result 입니다."
            "en" -> "the factorial of $n is $result"
            else -> "Unsupported language: $language"
        }
    }
}