package io.beaniejoy.coresecurity.domain.dto

import io.beaniejoy.coresecurity.domain.entity.Account

data class AccountDto(
    var username: String,
    var password: String,
    var email: String? = null,
    var age: Int? = null,
    var roles: List<String> = listOf(),
) {
    companion object {
        fun of(account: Account): AccountDto {
            return AccountDto(
                username = account.username!!,
                password = "",
                email = account.email,
                age = account.age,
                roles = account.userRoles.map { it.roleName!! }.toList()
            )
        }
    }
}