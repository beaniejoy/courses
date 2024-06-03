package com.example.feedserver.feed

import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestClient

@Service
class SocialFeedService(
    private val feedRepository: SocialFeedRepository,
    @Value("\${sns.user-server}")
    private val userServerUrl: String,
    private val restClient: RestClient = RestClient.create()
) {
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
        return feedRepository.save(feed.toEntity())
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