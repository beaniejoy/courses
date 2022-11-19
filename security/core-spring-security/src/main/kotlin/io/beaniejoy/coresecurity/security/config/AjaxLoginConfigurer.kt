package io.beaniejoy.coresecurity.security.config

import io.beaniejoy.coresecurity.security.filter.AjaxLoginProcessingFilter
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.web.HttpSecurityBuilder
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.authentication.RememberMeServices
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatcher

class AjaxLoginConfigurer<H : HttpSecurityBuilder<H>> :
    AbstractAuthenticationFilterConfigurer<H, AjaxLoginConfigurer<H>, AjaxLoginProcessingFilter>(
        AjaxLoginProcessingFilter(), null
    ) {

    private var successHandler: AuthenticationSuccessHandler? = null
    private var failureHandler: AuthenticationFailureHandler? = null
    private var authenticationManager: AuthenticationManager? = null

    override fun init(http: H) {
        super.init(http)
    }

    override fun configure(http: H) {
        if (this.authenticationManager == null) {
            this.authenticationManager = http.getSharedObject(AuthenticationManager::class.java)
        }

        authenticationFilter.apply {
            this.setAuthenticationManager(authenticationManager)
            this.setAuthenticationSuccessHandler(successHandler)
            this.setAuthenticationFailureHandler(failureHandler)

        }

        http.getSharedObject(SessionAuthenticationStrategy::class.java)?.run {
            authenticationFilter.setSessionAuthenticationStrategy(this)
        }

        http.getSharedObject(RememberMeServices::class.java)?.run {
            authenticationFilter.rememberMeServices = this
        }

        http.setSharedObject(AjaxLoginProcessingFilter::class.java, authenticationFilter)
        http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
    }

    override fun loginPage(loginPage: String?): AjaxLoginConfigurer<H> {
        return super.loginPage(loginPage)
    }

    fun successHandlerAjax(successHandler: AuthenticationSuccessHandler): AjaxLoginConfigurer<H> {
        this.successHandler = successHandler
        return this
    }

    fun failureHandlerAjax(failureHandler: AuthenticationFailureHandler): AjaxLoginConfigurer<H> {
        this.failureHandler = failureHandler
        return this
    }

    fun setAuthenticationManager(authenticationManager: AuthenticationManager): AjaxLoginConfigurer<H> {
        this.authenticationManager = authenticationManager
        return this
    }

    override fun createLoginProcessingUrlMatcher(loginProcessingUrl: String?): RequestMatcher {
        return AntPathRequestMatcher(loginProcessingUrl, "POST")
    }
}