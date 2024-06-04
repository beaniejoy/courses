package com.example.feedserver.config

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
class KafkaConfig {
    @Bean
    fun feedTopic(): NewTopic {
        return NewTopic("feed.created", 1, 1)
    }
}