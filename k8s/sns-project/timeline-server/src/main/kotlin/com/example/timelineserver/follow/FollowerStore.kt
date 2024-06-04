package com.example.timelineserver.follow

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component

@Component
class FollowerStore(
    private val redis: StringRedisTemplate,
    private val objectMapper: ObjectMapper
) {

    fun followUser(followMessage: FollowMessage) {
        if (followMessage.follow) {
            redis.opsForSet().add("user:follower:${followMessage.followerId}", followMessage.userId)
        } else {
            redis.opsForSet().remove("user:follower:${followMessage.followerId}", followMessage.userId)
        }
    }

    fun listFollower(userId: String): Set<String> {
        return redis.opsForSet().members("user:follower:$userId")
            ?: emptySet()
    }
}