package com.example.notificationbatch

import java.time.ZonedDateTime

data class NotificationInfo(
    val followId: Int,
    val email: String,
    val username: String,
    val followerName: String,
    val followerId: Int,
    val followDatetime: ZonedDateTime
)