package com.example.userserver.follow

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FollowService(
    private val followRepository: FollowRepository,
    private val kafkaTemplate: KafkaTemplate<String, String>
) {
    private val objectMapper = jacksonObjectMapper()

    fun isFollow(userId: Int, followerId: Int): Boolean {
        return followRepository.findByUserIdAndFollowerId(userId, followerId) != null
    }

    @Transactional
    fun followUser(userId: Int, followerId: Int): Follow? {
        if (isFollow(userId, followerId)) {
            return null
        }

        sendFollowerMessage(userId, followerId, true)

        return followRepository.save(Follow(userId = userId, followerId = followerId))
    }

    @Transactional
    fun unfollowUser(userId: Int, followerId: Int): Boolean {
        val follow = followRepository.findByUserIdAndFollowerId(userId, followerId)
            ?: return false

        sendFollowerMessage(userId, followerId, false)

        followRepository.delete(follow)

        return true
    }

    fun listFollower(userId: Int): List<Follow> {
        return followRepository.findFollowersByUserId(userId)
    }

    fun listFollowing(userId: Int): List<Follow> {
        return followRepository.findFollowingByUserId(userId)
    }

    private fun sendFollowerMessage(userId: Int, followerId: Int, isFollow: Boolean) {
        val message = FollowMessage(userId, followerId, isFollow)

        try {
            kafkaTemplate.send("user.follower", objectMapper.writeValueAsString(message))
        } catch (e: JsonProcessingException) {
            throw RuntimeException(e)
        }
    }
}