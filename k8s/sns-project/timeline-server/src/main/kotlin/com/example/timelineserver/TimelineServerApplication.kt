package com.example.timelineserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TimelineServerApplication

fun main(args: Array<String>) {
	runApplication<TimelineServerApplication>(*args)
}
