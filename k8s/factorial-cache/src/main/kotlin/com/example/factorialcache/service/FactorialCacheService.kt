package com.example.factorialcache.service

import com.example.factorialcache.util.FileLogger
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.annotation.PostConstruct
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.io.File
import java.io.IOException
import java.math.BigDecimal
import java.util.concurrent.ConcurrentHashMap

@Service
class FactorialCacheService(
    private val fileLogger: FileLogger,
    private val objectMapper: ObjectMapper
) {
    private val factorialMap: MutableMap<Int, BigDecimal> = ConcurrentHashMap()

    companion object {
        const val TEMP_CACHE_FILE =  "/factorial/cache/cache.json"
    }

    @PostConstruct
    fun loadCache() {
        File(TEMP_CACHE_FILE).also {
            if (it.exists()) {
                try {
                    val storedCache = objectMapper.readValue(
                        it,
                        object : TypeReference<Map<Int, BigDecimal>>() {}
                    )

                    factorialMap.putAll(storedCache)
                } catch (e: IOException) {
                    throw RuntimeException(e)
                }
            }
        }
    }

    fun cachedFactorial(n: Int): BigDecimal? {
        val result = factorialMap[n]

        fileLogger.log(result?.let { "Cache Hit ${n}!=${result}" } ?: "Cache missed")

        return result
    }

    fun cacheFactorial(n: Int, result: BigDecimal) {
        fileLogger.log("Cache factorial ${n}!=${result}")
        factorialMap[n] = result
    }

    @Scheduled(fixedDelay = 1_000)
    fun storeCache() {
        try {
            objectMapper.writeValue(File(TEMP_CACHE_FILE), factorialMap)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }
}