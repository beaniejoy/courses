package com.example.userserver.follow

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/follows")
class FollowController(
    private val followService: FollowService
) {
    @GetMapping("/followers/{userId}")
    fun listFollowers(@PathVariable("userId") userId: Int): List<Follow> {
        return followService.listFollower(userId)
    }

    @GetMapping("/followings/{userId}")
    fun listFollowings(@PathVariable("userId") userId: Int): List<Follow> {
        return followService.listFollowing(userId)
    }

    @GetMapping("/follow/{userId}/{followerId}")
    fun isFollow(
        @PathVariable("userId") userId: Int,
        @PathVariable("followerId") followerId: Int
    ): Boolean {
        return followService.isFollow(
            userId = userId,
            followerId = followerId
        )
    }

    @PostMapping("/follow")
    fun followUser(@RequestBody followRequest: FollowRequest): Follow? {
        return followService.followUser(
            userId = followRequest.userId,
            followerId = followRequest.followerId
        )
    }

    @PostMapping("/unfollow")
    fun unfollowUser(@RequestBody followRequest: FollowRequest): Boolean {
        return followService.unfollowUser(
            userId = followRequest.userId,
            followerId = followRequest.followerId
        )
    }
}