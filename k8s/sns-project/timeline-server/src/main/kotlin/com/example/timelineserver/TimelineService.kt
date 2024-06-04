package com.example.timelineserver

import com.example.timelineserver.feed.FeedStore
import com.example.timelineserver.follow.FollowerStore
import org.springframework.stereotype.Service
import kotlin.math.ceil
import kotlin.math.max

@Service
class TimelineService(
    private val feedStore: FeedStore,
    private val followerStore: FollowerStore
) {
    fun listUserFeed(userId: String): List<SocialPost> {
        val feedList = feedStore.listFeed(userId)
        val likes = feedStore.countLikes(feedList.map { it.feedId }.toList())

        return feedList.map { post ->
            SocialPost.of(post, likes.getOrDefault(post.feedId, 0))
        }
    }

    fun getRandomPost(userId: String, randomPost: Double): List<SocialPost> {
        val myPost: List<SocialPost> = if (userId == "none") listOf() else listMyFeed(userId)
        val randomPostSize = max(10, ceil(myPost.size * randomPost).toInt())

        val allPost = listAllFeed().toMutableList()

        val myPostIds = myPost.map { it.feedId }.toSet()

        allPost.removeIf { post -> myPostIds.contains(post.feedId) }

        val picked = if (randomPostSize >= allPost.size) {
            allPost
        } else {
            allPost.shuffle()
            allPost.subList(0, randomPostSize)
        }

        allPost.removeIf { post -> myPostIds.contains(post.feedId) }

        return (myPost + picked).sortedByDescending { it.uploadDatetime }
    }

    fun listFollowerFeed(followerSet: Set<String>): List<SocialPost> {
        return followerSet.map {
            listUserFeed(it)
        }.flatten()
    }

    fun listMyFeed(userId: String): List<SocialPost> {
        val followers = followerStore.listFollower(userId)
        val myPost = listUserFeed(userId)
        val followerFeed = listFollowerFeed(followers)

        return (myPost + followerFeed).sortedByDescending { it.uploadDatetime }
    }

    fun listAllFeed(): List<SocialPost> {
        val feedList = feedStore.allFeed()
        val likes = feedStore.countLikes(feedList.map { it.feedId }.toList())

        return feedList.map { post ->
            SocialPost.of(post, likes.getOrDefault(post.feedId, 0))
        }
    }

    fun likePost(userId: Int, postId: Int): Boolean {
        return if (feedStore.isLikePost(userId, postId)) {
            feedStore.unlikePost(userId, postId)
            false
        } else {
            feedStore.likePost(userId, postId)
            true
        }
    }

    fun countLike(postId: Int): Long {
        return feedStore.countLikes(postId)
    }
}