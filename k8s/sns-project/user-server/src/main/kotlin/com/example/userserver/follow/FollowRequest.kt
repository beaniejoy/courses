package com.example.userserver.follow

data class FollowRequest(
    val userId: Int,
    val followerId: Int
)