package com.example.userserver.user

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {
    @PostMapping
    fun signUpUser(@RequestBody userRequest: UserRequest): UserInfo {
        return userService.createUser(userRequest)
    }

    @GetMapping("/{id}")
    fun getUserInfo(@PathVariable("id") id: Int): ResponseEntity<UserInfo> {
        val user = userService.getUser(id)
            ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(user)
    }

    @GetMapping("/name/{name}")
    fun getUserInfoByName(@PathVariable("name") name: String): ResponseEntity<UserInfo> {
        val user = userService.getUserByName(name)
            ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(user)
    }

    @PostMapping("/signIn")
    fun signIn(@RequestBody userRequest: UserRequest): UserInfo? {
        return userService.signIn(userRequest)
    }
}