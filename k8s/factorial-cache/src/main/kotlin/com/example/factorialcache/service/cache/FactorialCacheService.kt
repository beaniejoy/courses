package com.example.factorialcache.service.cache

import java.math.BigDecimal

interface FactorialCacheService {
    fun cachedFactorial(n: Int): BigDecimal?

    fun cacheFactorial(n: Int, result: BigDecimal)
}