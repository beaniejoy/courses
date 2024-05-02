package com.example.factorialcache

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication
class FactorialCacheApplication

fun main(args: Array<String>) {
	runApplication<FactorialCacheApplication>(*args)
}
