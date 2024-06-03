package com.example.feedserver.feed

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/feeds")
class SocialFeedController(
    private val feedService: SocialFeedService
) {
    @GetMapping
    fun getAllFeeds(): List<FeedInfo> {
        val allFeeds = feedService.getAllFeeds()

        return allFeeds.map {
            val user = feedService.getUserInfo(it.uploaderId)
            FeedInfo.of(it, user.userName)
        }
    }

    @GetMapping("/user/{userId}")
    fun getAllFeedsByUser(@PathVariable("userId") userId: Int): List<SocialFeed> {
        return feedService.getAllFeedsByUploaderId(userId)
    }

    @GetMapping("/{id}")
    fun getFeedById(@PathVariable("id") id: Int): ResponseEntity<SocialFeed> {
        val feed = feedService.getFeedById(id)
            ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(feed)
    }

    @DeleteMapping("/{id}")
    fun deleteFeed(@PathVariable("id") id: Int): ResponseEntity<Void> {
        feedService.deleteFeed(id)
        return ResponseEntity.ok().build()
    }

    @PostMapping
    fun createFeed(@RequestBody feed: FeedRequest): SocialFeed {
        return feedService.createFeed(feed)
    }
}