package com.example.factorialcache.service

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service

@Service
class FactorialTaskService(
    private val redisTemplate: StringRedisTemplate
) {
    // 시간이 많이 걸리는 작업은 queue에 쌓도록
    fun saveCalculationTask(n: Int): Long {
        // 똑같은 n으로 들어왔어도 한 번만 처리되도록
        redisTemplate.opsForSet().add("factorial:task-queue", n.toString())

        return redisTemplate.opsForSet().size("factorial:task-queue") ?: 0L
    }
}