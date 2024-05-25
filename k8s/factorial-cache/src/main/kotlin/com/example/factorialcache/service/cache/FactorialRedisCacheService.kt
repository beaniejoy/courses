package com.example.factorialcache.service.cache

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class FactorialRedisCacheService(
    private val redisTemplate: StringRedisTemplate
) : FactorialCacheService {
    override fun cachedFactorial(n: Int): BigDecimal? {
        return redisTemplate.opsForHash<String, String>()
            .get("factorial:result-set", n.toString())?.let {
                BigDecimal(it)
            }
    }

    override fun cacheFactorial(n: Int, result: BigDecimal) {
        redisTemplate.opsForHash<String, String>()
            .put("factorial:result-set", n.toString(), result.toPlainString())
    }
}