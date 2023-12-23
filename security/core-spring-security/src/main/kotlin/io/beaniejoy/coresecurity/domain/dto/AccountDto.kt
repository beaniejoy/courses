package io.beaniejoy.coresecurity.domain.dto

import io.beaniejoy.coresecurity.domain.entity.Account

data class AccountDto(
    val id: Long = 0L,
    var username: String? = null,
    var password: String? = null,
    var email: String? = null,
    var age: Int? = null,
    var roles: List<String> = emptyList(),
) {
    companion object {
        fun of(account: Account): AccountDto {
            return AccountDto(
                id = account.id,
                username = account.username,
                password = "",
                email = account.email,
                age = account.age,
                roles = account.userRoles.map { it.roleName!! }.toList()
            )
        }
    }
}