package com.example.userserver.user

data class UserInfo(
    val userId: Int,
    val userName: String,
    val email: String
) {
    companion object {
        fun of(user: User): UserInfo = UserInfo(
            userId = user.userId,
            userName = user.username,
            email = user.email
        )
    }
}