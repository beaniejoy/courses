package io.beaniejoy.coresecurity.service

import io.beaniejoy.coresecurity.domain.entity.Account

interface UserService {
    fun createUser(account: Account)
}