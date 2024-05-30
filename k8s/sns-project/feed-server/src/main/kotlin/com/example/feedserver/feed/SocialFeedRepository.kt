package com.example.feedserver.feed

import org.springframework.data.jpa.repository.JpaRepository

interface SocialFeedRepository: JpaRepository<SocialFeed, Int> {
    fun findByUploaderId(uploaderId: Int): List<SocialFeed>
}
