package com.example.timelineserver.follow

data class FollowMessage(
    val userId: String,
    val followerId: String,
    val follow: Boolean
)