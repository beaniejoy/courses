package com.example.factorialcache.service

import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import java.math.BigDecimal

@Service
class FactorialCalculateService {
    private val factorialClient: RestClient = RestClient.create()

    fun getCalculatedResult(n: Int): BigDecimal {
        return factorialClient.get()
            .uri("http://factorial-app-service:8080/factorial?n=${n}")
            .retrieve()
            .onStatus(HttpStatusCode::isError) { _, response ->
                throw RuntimeException("invalid server response ${response.statusText}")
            }
            .body(String::class.java)
            .let { BigDecimal(it) }
    }
}