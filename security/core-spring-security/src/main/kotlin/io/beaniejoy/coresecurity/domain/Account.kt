package io.beaniejoy.coresecurity.domain

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class Account protected constructor(
    username: String,
    password: String,
    email: String,
    age: String,
    role: String
) {
    @Id
    @GeneratedValue
    val id: Long = 0L

    @Column(name = "username")
    var username: String? = username
        protected set

    @Column(name = "password")
    var password: String = password
        protected set

    @Column(name = "email")
    var email: String = email
        protected set

    @Column(name = "age")
    var age: String = age
        protected set

    @Column(name = "role")
    var role: String = role
        protected set

    companion object {
        fun createAccount(
            username: String,
            password: String,
            email: String,
            age: String,
            role: String
        ): Account {
            return Account(
                username = username,
                password = password,
                email = email,
                age = age,
                role = role
            )
        }
    }
}