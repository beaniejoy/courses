package com.example.userserver.user

import jakarta.persistence.*

@Entity
@Table
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,
    val username: String,
    val email: String,
    val password: String
) {
    companion object {
        fun of(username: String, email: String, password: String): User {
            return User(
                username = username,
                email = email,
                password = password
            )
        }
    }
}