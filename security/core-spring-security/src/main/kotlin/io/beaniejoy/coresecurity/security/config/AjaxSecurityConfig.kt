package io.beaniejoy.coresecurity.security.config

import io.beaniejoy.coresecurity.security.filter.AjaxLoginProcessingFilter
import io.beaniejoy.coresecurity.security.handler.AjaxAuthenticationFailureHandler
import io.beaniejoy.coresecurity.security.handler.AjaxAuthenticationSuccessHandler
import io.beaniejoy.coresecurity.security.provider.AjaxAuthenticationProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@Order(0)
class AjaxSecurityConfig {

    @Autowired
    lateinit var authenticationConfiguration: AuthenticationConfiguration

    @Autowired
    lateinit var ajaxAuthenticationProvider: AjaxAuthenticationProvider

    @Autowired
    lateinit var ajaxAuthenticationSuccessHandler: AjaxAuthenticationSuccessHandler

    @Autowired
    lateinit var ajaxAuthenticationFailureHandler: AjaxAuthenticationFailureHandler

    @Bean
    fun ajaxFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .antMatcher("/api/**")
            .authorizeRequests()
            .anyRequest().authenticated()

            .and()
            .addFilterBefore(ajaxLoginProcessingFilter(), UsernamePasswordAuthenticationFilter::class.java)

            .csrf().disable()
            .build()
    }

    @Bean
    fun authenticationManager(): AuthenticationManager {
        val authenticationManager = authenticationConfiguration.authenticationManager as ProviderManager
        authenticationManager.providers.add(ajaxAuthenticationProvider)
        return authenticationManager
    }

    // custom filter 적용시에는 거기에다가 AuthenticationManager, Success/FailureHandler를 따로 등록해줘야 한다.
    @Bean
    fun ajaxLoginProcessingFilter(): AjaxLoginProcessingFilter {
        return AjaxLoginProcessingFilter().apply {
            this.setAuthenticationManager(authenticationManager())
            this.setAuthenticationSuccessHandler(ajaxAuthenticationSuccessHandler)
            this.setAuthenticationFailureHandler(ajaxAuthenticationFailureHandler)
        }
    }
}