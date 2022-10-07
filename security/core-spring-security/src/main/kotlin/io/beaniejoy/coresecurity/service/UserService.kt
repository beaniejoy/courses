package io.beaniejoy.coresecurity.service

import io.beaniejoy.coresecurity.domain.Account

interface UserService {
    fun createUser(account: Account)
}