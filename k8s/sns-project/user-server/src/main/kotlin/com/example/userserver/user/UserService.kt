package com.example.userserver.user

import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    fun createUser(userRequest: UserRequest): UserInfo {
        check(userRepository.findByUsername(userRequest.username!!) == null) {
            throw RuntimeException("username duplicated")
        }

        val hashPassword = passwordEncoder.encode(userRequest.plainPassword)
        val user = User.of(
            username = userRequest.username,
            email = userRequest.email,
            password = hashPassword,
        )

        val savedUser = userRepository.save(user)

        return UserInfo.of(savedUser)
    }

    fun getUser(userId: Int): UserInfo? {
        return userRepository.findByIdOrNull(userId)?.let {
            UserInfo.of(it)
        }
    }

    fun getUserByName(name: String): UserInfo? {
        return userRepository.findByUsername(name)?.let {
            UserInfo.of(it)
        }
    }

    fun signIn(userRequest: UserRequest): UserInfo? {
        val user = userRequest.username?.let {
            userRepository.findByUsername(it)
        } ?: return null

        if (passwordEncoder.matches(userRequest.plainPassword, user.password)) {
            return UserInfo.of(user)
        }

        return null
    }
}