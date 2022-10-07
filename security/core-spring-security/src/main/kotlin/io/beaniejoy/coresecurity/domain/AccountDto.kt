package io.beaniejoy.coresecurity.domain

data class AccountDto(
    var username: String,
    var password: String,
    var email: String,
    var age: String,
    var role: String
)