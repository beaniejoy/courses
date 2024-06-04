package com.example.timelineserver

import com.example.timelineserver.feed.FeedInfo
import java.time.ZonedDateTime

data class SocialPost(
    val feedId: Int,
    val imageId: String,
    val uploaderName: String,
    val uploaderId: Int,

    val uploadDatetime: ZonedDateTime,
    val contents: String,
    val likes: Long
) {
    companion object {
        fun of(post: FeedInfo, likes: Long): SocialPost {
            return SocialPost(
                feedId = post.feedId,
                imageId = post.imageId,
                uploaderId = post.uploaderId,
                uploaderName = post.uploaderName,
                uploadDatetime = post.uploadDatetime,
                contents = post.contents,
                likes = likes
            )
        }
    }
}