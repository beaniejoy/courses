package com.example.feedserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FeedServerApplication

fun main(args: Array<String>) {
	runApplication<FeedServerApplication>(*args)
}
