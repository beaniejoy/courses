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
        http
            .antMatcher("/api/**")
            .authorizeRequests()
//            .antMatchers("/api/messages").hasRole("MANAGER")
            .antMatchers("/api/login").permitAll()
            .anyRequest().authenticated()

            .and()
            .exceptionHandling()
            .authenticationEntryPoint(ajaxLoginAuthenticationEntryPoint)    // ???????????? ?????? ???????????? ?????? ?????? ?????? ??????
            .accessDeniedHandler(ajaxAccessDeniedHandler)                   // ?????? ?????? ???????????? ???????????? ?????? ?????? ????????? ?????? ?????? ??????
            .and()
            // ### custom DSL ?????? ????????? ###
//            .addFilterBefore(ajaxLoginProcessingFilter(), UsernamePasswordAuthenticationFilter::class.java)
            // login page ?????? csrf token ?????? on
//            .csrf().disable()

        return customConfigurerAjax(http)
            .and()
            .build()
    }

    private fun customConfigurerAjax(http: HttpSecurity): AjaxLoginConfigurer<HttpSecurity> {
        return http
            .apply(AjaxLoginConfigurer())
            .successHandlerAjax(ajaxAuthenticationSuccessHandler)
            .failureHandlerAjax(ajaxAuthenticationFailureHandler)
            .loginProcessingUrl("/api/login")
            .setAuthenticationManager(authenticationManager())
    }

    @Bean
    fun authenticationManager(): AuthenticationManager {
        val authenticationManager = authenticationConfiguration.authenticationManager as ProviderManager
        authenticationManager.providers.add(ajaxAuthenticationProvider)
        return authenticationManager
    }

    // ### custom DSL ?????? ????????? ###
    // custom filter ??????????????? ??????????????? AuthenticationManager, Success/FailureHandler??? ?????? ??????????????? ??????.
//    @Bean
//    fun ajaxLoginProcessingFilter(): AjaxLoginProcessingFilter {
//        return AjaxLoginProcessingFilter().apply {
//            this.setAuthenticationManager(authenticationManager())
//            this.setAuthenticationSuccessHandler(ajaxAuthenticationSuccessHandler)
//            this.setAuthenticationFailureHandler(ajaxAuthenticationFailureHandler)
//        }
//    }
}