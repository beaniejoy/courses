package io.beaniejoy.coresecurity.security.service

import io.beaniejoy.coresecurity.domain.Account
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User

class AccountContext(
    val account: Account,
    authorities: Collection<GrantedAuthority>
): User(account.username, account.password, authorities)