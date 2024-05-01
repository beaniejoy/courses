package com.example.factorialcache

import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.concurrent.ConcurrentHashMap

@Service
class FactorialCacheService {
    private val factorialMap: MutableMap<Int, BigDecimal> = ConcurrentHashMap()

    fun cachedFactorial(n: Int): BigDecimal? {
        return factorialMap[n]
    }

    fun cacheFactorial(n: Int, result: BigDecimal) {
        factorialMap[n] = result
    }
}