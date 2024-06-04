package com.example.feedserver.feed

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatusCode
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestClient

@Service
class SocialFeedService(
    private val feedRepository: SocialFeedRepository,
    @Value("\${sns.user-server}")
    private val userServerUrl: String,
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper
) {
    private val restClient: RestClient = RestClient.create()

    fun getAllFeeds(): List<SocialFeed> {
        return feedRepository.findAll()
    }

    fun getAllFeedsByUploaderId(uploaderId: Int): List<SocialFeed> {
        return feedRepository.findByUploaderId(uploaderId)
    }

    fun getFeedById(feedId: Int): SocialFeed? {
        return feedRepository.findByIdOrNull(feedId)
    }

    fun deleteFeed(feedId: Int) {
        feedRepository.deleteById(feedId)
    }

    @Transactional
    fun createFeed(feed: FeedRequest): SocialFeed {
        val savedFeed = feedRepository.save(feed.toEntity())
        val uploader = getUserInfo(savedFeed.uploaderId)
        val feedInfo = FeedInfo.of(savedFeed, uploader.userName)

        try {
            kafkaTemplate.send("feed.created", objectMapper.writeValueAsString(feedInfo))
        } catch (e: JsonProcessingException) {
            throw RuntimeException(e)
        }

        return savedFeed
    }

    fun getUserInfo(userId: Int): UserInfo {
        return restClient.get()
            .uri("${userServerUrl}/api/users/$userId")
            .retrieve()
            .onStatus(HttpStatusCode::isError) { _, response ->
                throw RuntimeException("invalid server response ${response.statusCode}")
            }
            .body(UserInfo::class.java)
            ?: throw RuntimeException("not found user")
    }
}