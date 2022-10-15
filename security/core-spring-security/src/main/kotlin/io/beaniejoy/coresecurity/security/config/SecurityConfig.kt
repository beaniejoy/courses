package io.beaniejoy.coresecurity.security.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationDetailsSource
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import javax.servlet.http.HttpServletRequest

@Configuration
@EnableWebSecurity
class SecurityConfig {
    @Autowired
    lateinit var authenticationDetailsSource: AuthenticationDetailsSource<HttpServletRequest, *>

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .authorizeRequests()
            .antMatchers("/", "/users").permitAll()
            .antMatchers("/mypage").hasRole("USER")
            .antMatchers("/messages").hasRole("MANAGER")
            .antMatchers("/config").hasRole("ADMIN")
            .anyRequest().authenticated()

            .and()
            .formLogin()
            .loginPage("/login")
            .loginProcessingUrl("/login_proc")
            .authenticationDetailsSource(authenticationDetailsSource)
            .defaultSuccessUrl("/")
            .permitAll()

            .and().build()
    }

    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer {
        return WebSecurityCustomizer { web ->
            web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                .antMatchers("/error")
        }
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder()
    }
}