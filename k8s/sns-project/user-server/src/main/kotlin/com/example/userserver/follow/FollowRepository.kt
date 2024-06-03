package com.example.userserver.follow

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface FollowRepository : JpaRepository<Follow, Int> {
    fun findByUserIdAndFollowerId(userId: Int, followerId: Int): Follow?

    @Query(value = "SELECT new com.example.userserver.user.UserInfo(u.userId, u.username, u.email) FROM Follow f, User u WHERE f.userId = :userId AND u.userId = f.followerId")
    fun findFollowersByUserId(@Param("userId") userId: Int): List<Follow>

    @Query(value = "SELECT new com.example.userserver.user.UserInfo(u.userId, u.username, u.email) FROM Follow f, User u WHERE f.followerId = :userId AND u.userId = f.userId")
    fun findFollowingByUserId(@Param("userId") userId: Int): List<Follow>
}