package io.beaniejoy.coresecurity.security.filter

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.beaniejoy.coresecurity.domain.dto.AccountDto
import io.beaniejoy.coresecurity.security.token.AjaxAuthenticationToken
import io.beaniejoy.coresecurity.util.WebUtil
import mu.KotlinLogging
import org.springframework.http.HttpMethod
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.authentication.WebAuthenticationDetails
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.util.StringUtils
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AjaxLoginProcessingFilter :
    AbstractAuthenticationProcessingFilter(AntPathRequestMatcher("/api/login", "POST")) {

    private val log = KotlinLogging.logger {}
    private val objectMapper = jacksonObjectMapper()

    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication? {

        log.info { "###### AjaxLoginProcessingFilter ######" }

        if (HttpMethod.POST.name != request.method || WebUtil.isAjax(request).not()) {
            throw IllegalStateException("Authentication is not supported")
        }

        val accountDto = objectMapper.readValue(request.reader, AccountDto::class.java)
        if (StringUtils.hasText(accountDto.username).not() || StringUtils.hasText(accountDto.password).not()) {
            throw IllegalArgumentException("Username & Password are not empty!!")
        }

        val token = AjaxAuthenticationToken(accountDto.username!!, accountDto.password!!).apply {
            this.details = WebAuthenticationDetails(request)
        }

        return this.authenticationManager.authenticate(token)
    }
}