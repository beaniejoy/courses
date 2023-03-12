package io.beaniejoy.springdatajpa.repository

import org.springframework.beans.factory.annotation.Value

interface UsernameOnly {
//    @Value("#{target.username + ' ' + target.age}") // open projection
    fun getUsername(): String
}