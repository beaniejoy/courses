package com.example.timelineserver.feed

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class FeedListener(
    private val objectMapper: ObjectMapper,
    private val feedStore: FeedStore
) {
    @KafkaListener(topics = ["feed.created"], groupId = "timeline-server")
    fun listen(message: String) {
        val feed = objectMapper.readValue(message, FeedInfo::class.java)
        feedStore.savePost(feed)
    }
}