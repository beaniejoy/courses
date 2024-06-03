package com.example.userserver.user

data class UserRequest(
    val username: String?,
    val email: String,
    val plainPassword: String
)