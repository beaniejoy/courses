package io.beaniejoy.coresecurity.security.provider

import io.beaniejoy.coresecurity.security.common.FormWebAuthenticationDetails
import io.beaniejoy.coresecurity.security.service.AccountContext
import mu.KLogging
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.InsufficientAuthenticationException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

// bean 등록만으로 가능
@Component
class FormAuthenticationProvider(
    private val userDetailsService: UserDetailsService,
    private val passwordEncoder: PasswordEncoder
): AuthenticationProvider {

    companion object: KLogging()

    // AuthenticationManager 에서 전달 받은 Authentication 객체
    // loadUserByUsername, 실제 인증 처리까지 여기서 담당
    override fun authenticate(authentication: Authentication): Authentication {
        val username = authentication.name
        val password = authentication.credentials as String

        val accountContext = userDetailsService.loadUserByUsername(username) as AccountContext

        if (passwordEncoder.matches(password, accountContext.account.password).not()) {
            throw BadCredentialsException("Invalid Password")
        }

        val details = authentication.details as FormWebAuthenticationDetails
        val secretKey = details.secretKey
        if (secretKey == null || "secret" != secretKey) {
            throw InsufficientAuthenticationException("InsufficientAuthenticationException")
        }

        logger.info { "[FormAuthenticationProvider] authenticate Completed!!" }

        return UsernamePasswordAuthenticationToken(accountContext.account, null, accountContext.authorities)
    }

    override fun supports(authentication: Class<*>): Boolean {
        return UsernamePasswordAuthenticationToken::class.java.isAssignableFrom(authentication)
    }
}