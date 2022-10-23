package io.beaniejoy.coresecurity.security.handler

import mu.KLogging
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class AjaxAccessDeniedHandler: AccessDeniedHandler {
    companion object: KLogging()

    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException,
    ) {
        logger.info { "########## AjaxAccessDeniedHandler access denied" }

        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access is denied")
    }
}