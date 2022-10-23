package io.beaniejoy.coresecurity.security.common

import mu.KLogging
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class AjaxLoginAuthenticationEntryPoint: AuthenticationEntryPoint {
    companion object: KLogging()

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException,
    ) {
        logger.info { "########## AjaxLoginAuthenticationEntryPoint unauthorized" }

        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")
    }
}