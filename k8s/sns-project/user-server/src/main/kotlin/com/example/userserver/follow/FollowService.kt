package com.example.userserver.follow

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FollowService(
    private val followRepository: FollowRepository
) {
    fun isFollow(userId: Int, followerId: Int): Boolean {
        return followRepository.findByUserIdAndFollowerId(userId, followerId) != null
    }

    @Transactional
    fun followUser(userId: Int, followerId: Int): Follow? {
        if (isFollow(userId, followerId)) {
            return null
        }

        return followRepository.save(Follow(userId = userId, followerId = followerId))
    }

    @Transactional
    fun unfollowUser(userId: Int, followerId: Int): Boolean {
        val follow = followRepository.findByUserIdAndFollowerId(userId, followerId)
            ?: return false

        followRepository.delete(follow)

        return true
    }

    fun listFollower(userId: Int): List<Follow> {
        return followRepository.findFollowersByUserId(userId)
    }

    fun listFollowing(userId: Int): List<Follow> {
        return followRepository.findFollowingByUserId(userId)
    }
}