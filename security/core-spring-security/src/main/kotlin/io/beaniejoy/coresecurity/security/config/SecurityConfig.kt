package io.beaniejoy.coresecurity.security.config

import io.beaniejoy.coresecurity.security.handler.FormAccessDeniedHandler
import io.beaniejoy.coresecurity.security.handler.FormAuthenticationFailureHandler
import io.beaniejoy.coresecurity.security.handler.FormAuthenticationSuccessHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.authentication.AuthenticationDetailsSource
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import javax.servlet.http.HttpServletRequest


@Configuration
@Order(1)
class SecurityConfig {
    @Autowired
    lateinit var authenticationDetailsSource: AuthenticationDetailsSource<HttpServletRequest, *>

    @Autowired
    lateinit var formAuthenticationSuccessHandler: FormAuthenticationSuccessHandler

    @Autowired
    lateinit var formAuthenticationFailureHandler: FormAuthenticationFailureHandler

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .authorizeRequests()
            .antMatchers("/", "/users", "user/login/**", "/login*").permitAll()
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
            .successHandler(formAuthenticationSuccessHandler)
            .failureHandler(formAuthenticationFailureHandler)
            .permitAll()

            .and()
            .exceptionHandling()
            .accessDeniedHandler(accessDeniedHandler())
            .and().build()
    }

    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer {
        return WebSecurityCustomizer { web ->
            web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations())
            web.ignoring().antMatchers("/error")
        }
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder()
    }

    @Bean
    fun accessDeniedHandler(): FormAccessDeniedHandler {
        return FormAccessDeniedHandler("/denied")
    }
}