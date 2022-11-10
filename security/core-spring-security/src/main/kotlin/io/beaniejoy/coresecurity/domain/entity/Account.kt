package io.beaniejoy.coresecurity.domain.entity

import javax.persistence.*

@Entity
class Account protected constructor(
    username: String? = null,
    password: String? = null,
    email: String? = null,
    age: Int? = null
) {
    @Id
    @GeneratedValue
    val id: Long = 0L

    @Column(name = "username")
    var username: String? = username
        protected set

    @Column(name = "password")
    var password: String? = password
        protected set

    @Column(name = "email")
    var email: String? = email
        protected set

    @Column(name = "age")
    var age: Int? = age
        protected set

    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinTable(
        name = "account_roles",
        joinColumns = [JoinColumn(name = "account_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")]
    )
    var userRoles: MutableSet<Role> = HashSet()
        protected set

    companion object {
        fun createAccount(
            username: String,
            password: String,
            email: String,
            age: Int
        ): Account {
            return Account(
                username = username,
                password = password,
                email = email,
                age = age
            )
        }

        fun empty(): Account {
            return Account()
        }
    }

    fun addRoles(roles: HashSet<Role>) {
        this.userRoles.addAll(roles)
    }

    fun modifyUserRoles(userRoles: HashSet<Role>) {
        if (userRoles.isEmpty()) {
            return
        }

        this.userRoles = userRoles
    }
}