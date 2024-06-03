package com.example.feedserver.feed

import java.time.ZonedDateTime

data class FeedInfo(
    val feedId: Int,
    val imageId: String,
    val uploaderId: Int,
    val uploaderName: String,
    var uploadDatetime: ZonedDateTime?,
    val contents: String
) {
    companion object {
        fun of(socialFeed: SocialFeed, uploaderName: String): FeedInfo {
            return FeedInfo(
                feedId = socialFeed.feedId,
                imageId = socialFeed.imageId,
                uploaderId = socialFeed.uploaderId,
                uploaderName = uploaderName,
                uploadDatetime = socialFeed.uploadDatetime,
                contents = socialFeed.contents
            )
        }
    }
}