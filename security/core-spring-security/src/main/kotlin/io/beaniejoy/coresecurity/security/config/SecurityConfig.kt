package io.beaniejoy.coresecurity.security.config

import io.beaniejoy.coresecurity.security.filter.AjaxLoginProcessingFilter
import io.beaniejoy.coresecurity.security.handler.CustomAccessDeniedHandler
import io.beaniejoy.coresecurity.security.handler.CustomAuthenticationFailureHandler
import io.beaniejoy.coresecurity.security.handler.CustomAuthenticationSuccessHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationDetailsSource
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import javax.servlet.http.HttpServletRequest


@Configuration
@EnableWebSecurity
class SecurityConfig {
    @Autowired
    lateinit var authenticationDetailsSource: AuthenticationDetailsSource<HttpServletRequest, *>

    @Autowired
    lateinit var customAuthenticationSuccessHandler: CustomAuthenticationSuccessHandler

    @Autowired
    lateinit var customAuthenticationFailureHandler: CustomAuthenticationFailureHandler

    @Autowired
    lateinit var authenticationConfiguration: AuthenticationConfiguration

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
            .successHandler(customAuthenticationSuccessHandler)
            .failureHandler(customAuthenticationFailureHandler)
            .permitAll()

            .and()
            .exceptionHandling()
            .accessDeniedHandler(accessDeniedHandler())

            .and()
            .addFilterBefore(ajaxLoginProcessingFilter(), UsernamePasswordAuthenticationFilter::class.java)

            .csrf().disable()
            .build()
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
    fun accessDeniedHandler(): CustomAccessDeniedHandler {
        return CustomAccessDeniedHandler("/denied")
    }

    @Bean
    fun authenticationManager(): AuthenticationManager {
        return authenticationConfiguration.authenticationManager
    }

    @Bean
    fun ajaxLoginProcessingFilter(): AjaxLoginProcessingFilter {
        return AjaxLoginProcessingFilter().apply {
            this.setAuthenticationManager(authenticationManager())
        }
    }
}