package com.example.userserver.config

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
class KafkaConfig {
    @Bean
    fun userFollowerTopic(): NewTopic {
        return NewTopic("user.follower", 1, 1)
    }
}