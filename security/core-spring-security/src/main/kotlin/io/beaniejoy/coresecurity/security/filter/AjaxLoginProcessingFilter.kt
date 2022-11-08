package io.beaniejoy.coresecurity.security.filter

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.beaniejoy.coresecurity.domain.AccountDto
import io.beaniejoy.coresecurity.security.token.AjaxAuthenticationToken
import mu.KotlinLogging
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.util.StringUtils
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AjaxLoginProcessingFilter :
    AbstractAuthenticationProcessingFilter(AntPathRequestMatcher("/api/login", "POST")) {

    private val log = KotlinLogging.logger {}
    private val objectMapper = jacksonObjectMapper()

    companion object {
        const val XML_HTTP_REQUEST = "XMLHttpRequest"
        const val X_REQUESTED_WITH = "X-Requested-With"
    }

    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication? {

        log.info { "###### AjaxLoginProcessingFilter ######" }

        if (isAjax(request).not()) {
            throw IllegalStateException("Authentication is not supported")
        }

        val accountDto = objectMapper.readValue(request.reader, AccountDto::class.java)
        if (StringUtils.hasText(accountDto.username).not() || StringUtils.hasText(accountDto.password).not()) {
            throw IllegalArgumentException("Username & Password are not empty!!")
        }

        val token = AjaxAuthenticationToken(accountDto.username, accountDto.password)

        return this.authenticationManager.authenticate(token)
    }

    private fun isAjax(request: HttpServletRequest): Boolean {
        if (XML_HTTP_REQUEST == request.getHeader(X_REQUESTED_WITH)) {
            return true
        }

        return false
    }
}