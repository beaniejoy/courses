package com.example.factorialapp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FactorialAppApplication

fun main(args: Array<String>) {
    runApplication<FactorialAppApplication>(*args)
}
