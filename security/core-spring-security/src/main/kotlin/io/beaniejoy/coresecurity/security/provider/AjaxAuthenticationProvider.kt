package io.beaniejoy.coresecurity.security.provider

import io.beaniejoy.coresecurity.security.service.AccountContext
import io.beaniejoy.coresecurity.security.token.AjaxAuthenticationToken
import mu.KLogging
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class AjaxAuthenticationProvider(
    private val userDetailsService: UserDetailsService,
    private val passwordEncoder: PasswordEncoder
): AuthenticationProvider {
    companion object: KLogging()

    override fun authenticate(authentication: Authentication): Authentication {
        logger.info { "[AjaxAuthenticationProvider] authenticate Start!!" }

        val username = authentication.name
        val password = authentication.credentials as String?    // AjaxAuthenticationToken getCredentials() nullable 하기에

        val accountContext = userDetailsService.loadUserByUsername(username) as AccountContext

        if (passwordEncoder.matches(password, accountContext.account.password).not()) {
            throw BadCredentialsException("Invalid Password")
        }

        logger.info { "[AjaxAuthenticationProvider] authenticate Completed!!" }

        return AjaxAuthenticationToken(accountContext.account, null, accountContext.authorities)
    }

    override fun supports(authentication: Class<*>): Boolean {
        return AjaxAuthenticationToken::class.java.isAssignableFrom(authentication)
    }
}