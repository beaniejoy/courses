package com.example.feedserver.feed

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class SocialFeedService(
    private val feedRepository: SocialFeedRepository
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

    fun createFeed(feed: FeedRequest): SocialFeed {
        return feedRepository.save(feed.toEntity())
    }
}