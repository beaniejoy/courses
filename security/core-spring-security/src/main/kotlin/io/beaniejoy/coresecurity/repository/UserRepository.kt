package io.beaniejoy.coresecurity.repository

import io.beaniejoy.coresecurity.domain.entity.Account
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository: JpaRepository<Account, Long> {
    fun findByUsername(username: String): Account?
}