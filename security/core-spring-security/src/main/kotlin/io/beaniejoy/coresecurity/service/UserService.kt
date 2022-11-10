package io.beaniejoy.coresecurity.service

import io.beaniejoy.coresecurity.domain.dto.AccountDto
import io.beaniejoy.coresecurity.domain.entity.Account

interface UserService {
    fun createUser(account: Account)

    fun modifyUser(accountDto: AccountDto)

    fun getUsers(): List<Account>

    fun getUser(id: Long): AccountDto

    fun deleteUser(id: Long)
}