package com.example.timelineserver.follow

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class FollowerListener(
    private val objectMapper: ObjectMapper,
    private val followerStore: FollowerStore
) {
    @KafkaListener(topics = ["user.follower"], groupId = "timeline-server")
    fun listen(message: String) {
        val followMessage = objectMapper.readValue(message, FollowMessage::class.java)
        followerStore.followUser(followMessage)
    }
}