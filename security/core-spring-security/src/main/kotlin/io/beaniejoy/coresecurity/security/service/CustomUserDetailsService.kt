package io.beaniejoy.coresecurity.security.service

import io.beaniejoy.coresecurity.repository.UserRepository
import mu.KLogging
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component

// bean 객체로 지정만 하면 Spring Security에서 알아서 UserDetailsService로 사용
@Component
class CustomUserDetailsService(
    private val userRepository: UserRepository
): UserDetailsService {
    companion object: KLogging()

    override fun loadUserByUsername(username: String): UserDetails {
        val account = userRepository.findByUsername(username)
            ?: throw UsernameNotFoundException("UsernameNotFoundException")

        val authorities: List<GrantedAuthority> = account.userRoles.map {
            SimpleGrantedAuthority(it.roleName!!)
        }.toList()

        logger.info { "[CustomUserDetailsService] account ${account.username}, roles ${authorities}" }

        return AccountContext(account, authorities)
    }
}