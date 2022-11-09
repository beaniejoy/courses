package io.beaniejoy.coresecurity.domain.dto

data class AccountDto(
    var username: String,
    var password: String,
    var email: String? = null,
    var age: String? = null,
    var role: String? = null
)