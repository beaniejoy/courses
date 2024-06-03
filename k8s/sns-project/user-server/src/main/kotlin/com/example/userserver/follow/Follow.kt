package com.example.userserver.follow

import jakarta.persistence.*
import java.time.ZonedDateTime

@Entity
@Table
class Follow(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val followId: Int = 0,
    val userId: Int,
    val followerId: Int,
    @Temporal(TemporalType.TIMESTAMP)
    var followDateTime: ZonedDateTime? = null,
) {
    @PrePersist
    fun onCreate() {
        followDateTime = ZonedDateTime.now()
    }
}