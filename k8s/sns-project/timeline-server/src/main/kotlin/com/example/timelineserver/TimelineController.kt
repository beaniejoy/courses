package com.example.timelineserver

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/timeline")
class TimelineController(
    private val timelineService: TimelineService
) {
    @GetMapping
    fun listAllFeed(): List<SocialPost> {
        return timelineService.listAllFeed()
    }

    @GetMapping("/{userId}")
    fun listFeed(
        @PathVariable("userId") userId: String,
        @RequestParam(value = "followingFeed", required = false)
        includeFollowingFeed: Boolean,
        @RequestParam(value = "randomPost", required = false, defaultValue = "0")
        randomPost: Double
    ): List<SocialPost> {
        if (randomPost > 0) {
            return timelineService.getRandomPost(userId, randomPost)
        }

        return if (includeFollowingFeed)
            timelineService.listMyFeed(userId)
        else
            timelineService.listUserFeed(userId)
    }

    @GetMapping("/like/{postId}/{userId}")
    fun likePost(
        @PathVariable("postId") postId: Int,
        @PathVariable("userId") userId: Int
    ): LikeResponse {
        val isLike = timelineService.likePost(userId, postId)
        val count = timelineService.countLike(postId)

        return LikeResponse(count, isLike)
    }
}