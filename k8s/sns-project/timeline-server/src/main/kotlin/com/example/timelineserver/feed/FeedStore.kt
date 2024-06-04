package com.example.timelineserver.feed

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.redis.connection.StringRedisConnection
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component

@Component
class FeedStore(
    private val redis: StringRedisTemplate,
    private val objectMapper: ObjectMapper
) {
    fun savePost(post: FeedInfo) {
        // sorted set
        redis.opsForZSet().add(
            "feed:${post.uploaderId}",
            objectMapper.writeValueAsString(post),
            post.uploadDatetime.toEpochSecond().toDouble()
        )

        redis.opsForZSet().add(
            "feed:all",
            objectMapper.writeValueAsString(post),
            post.uploadDatetime.toEpochSecond().toDouble()
        )
    }

    fun allFeed(): List<FeedInfo> {
        val savedFeed = redis.opsForZSet().reverseRange("feed:all", 0, -1)
            ?: emptySet()

        return savedFeed.map { feed ->
            objectMapper.readValue(feed, FeedInfo::class.java)
        }
    }

    fun listFeed(userId: String): List<FeedInfo> {
        val savedFeed = redis.opsForZSet().reverseRange("feed:$userId", 0, -1)
            ?: emptySet()

        return savedFeed.map { feed ->
            objectMapper.readValue(feed, FeedInfo::class.java)
        }
    }

    fun likePost(userId: Int, postId: Int): Long? {
        return redis.opsForSet().add("likes:$postId", userId.toString())
    }

    fun unlikePost(userId: Int, postId: Int): Long? {
        return redis.opsForSet().remove("likes:$postId", userId.toString())
    }

    fun isLikePost(userId: Int, postId: Int): Boolean {
        return redis.opsForSet().isMember("likes:$postId", userId.toString())
            ?: false
    }

    fun countLikes(postId: Int): Long {
        return redis.opsForSet().size("likes:$postId") ?: 0L
    }

    fun countLikes(postIds: List<Int>): Map<Int, Long> {
        val results = redis.executePipelined { connection ->
            val stringRedisConn = connection as StringRedisConnection

            postIds.forEach {
                stringRedisConn.sCard("likes$it")
            }

            null
        }

        return postIds.mapIndexed { index, postId ->
            postId to results[index] as Long
        }.toMap()
    }
}