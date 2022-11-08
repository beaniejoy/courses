package io.beaniejoy.coresecurity.security.config

import io.beaniejoy.coresecurity.security.common.AjaxLoginAuthenticationEntryPoint
import io.beaniejoy.coresecurity.security.filter.AjaxLoginProcessingFilter
import io.beaniejoy.coresecurity.security.handler.AjaxAccessDeniedHandler
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

    @Autowired
    lateinit var ajaxLoginAuthenticationEntryPoint: AjaxLoginAuthenticationEntryPoint

    @Autowired
    lateinit var ajaxAccessDeniedHandler: AjaxAccessDeniedHandler

    @Bean
    fun ajaxFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .antMatcher("/api/**")
            .authorizeRequests()
            .antMatchers("/api/messages").hasRole("MANAGER")
            .antMatchers("/api/login").permitAll()
            .anyRequest().authenticated()

            .and()
            .addFilterBefore(ajaxLoginProcessingFilter(), UsernamePasswordAuthenticationFilter::class.java)

            .exceptionHandling()
            .authenticationEntryPoint(ajaxLoginAuthenticationEntryPoint)    // 인증받지 않은 사용자에 대한 인가 예외 처리
            .accessDeniedHandler(ajaxAccessDeniedHandler)                   // 인증 받은 사용자의 허용받지 않은 자원 접근에 대한 예외 처리

            .and()
//            .csrf().disable()
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