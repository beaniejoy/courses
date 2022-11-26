package io.beaniejoy.coresecurity.domain.dto

import io.beaniejoy.coresecurity.domain.entity.Account

data class AccountDto(
    val id: Long,
    var username: String,
    var password: String,
    var email: String? = null,
    var age: Int? = null,
    var roles: List<String> = listOf(),
) {
    companion object {
        fun of(account: Account): AccountDto {
            return AccountDto(
                id = account.id,
                username = account.username!!,
                password = "",
                email = account.email,
                age = account.age,
                roles = account.userRoles.map { it.roleName!! }.toList()
            )
        }
    }
}