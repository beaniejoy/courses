package com.example.timelineserver.feed

import java.time.ZonedDateTime

data class FeedInfo(
    val feedId: Int,
    val imageId: String,
    val uploaderId: Int,
    val uploaderName: String,
    val uploadDatetime: ZonedDateTime,
    val contents: String
)