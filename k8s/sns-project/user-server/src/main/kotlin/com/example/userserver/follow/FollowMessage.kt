package com.example.userserver.follow

data class FollowMessage(
    val userId: Int,
    val followerId: Int,
    val follow: Boolean
)