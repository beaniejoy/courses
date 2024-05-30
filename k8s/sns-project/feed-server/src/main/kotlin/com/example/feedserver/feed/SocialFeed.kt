package com.example.feedserver.feed

import jakarta.persistence.*
import java.time.ZonedDateTime

@Entity
@Table
class SocialFeed(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val feedId: Int = 0,

    val imageId: String,

    val uploaderId: Int,

    @Temporal(TemporalType.TIMESTAMP)
    var uploadDatetime: ZonedDateTime? = null,

    val contents: String
) {
    @PrePersist
    fun onCreate() {
        uploadDatetime = ZonedDateTime.now()
    }
}