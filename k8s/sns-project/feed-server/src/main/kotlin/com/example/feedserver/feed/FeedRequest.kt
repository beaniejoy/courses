package com.example.feedserver.feed

data class FeedRequest(
    val imageId: String,
    val uploaderId: Int,
    val contents: String
) {
    fun toEntity(): SocialFeed {
        return SocialFeed(
            imageId = imageId,
            uploaderId = uploaderId,
            contents = contents
        )
    }
}