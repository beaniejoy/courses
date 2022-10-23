package io.beaniejoy.coresecurity.security.handler

import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class FormAccessDeniedHandler(
    private val errorPage: String
): AccessDeniedHandler {
    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException,
    ) {
        val deniedUrl = "${errorPage}?exception=${accessDeniedException.message}"
        response.sendRedirect(deniedUrl)
    }
}