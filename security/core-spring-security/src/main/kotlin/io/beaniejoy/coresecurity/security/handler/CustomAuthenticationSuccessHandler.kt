package io.beaniejoy.coresecurity.security.handler

import mu.KLogging
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.security.web.savedrequest.HttpSessionRequestCache
import org.springframework.security.web.savedrequest.RequestCache
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class CustomAuthenticationSuccessHandler : SimpleUrlAuthenticationSuccessHandler() {
    private val requestCache: RequestCache = HttpSessionRequestCache()

    companion object : KLogging()

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication,
    ) {
        defaultTargetUrl = "/"

        // 인증이 되지 않은 상태로 진입했던 이전 페이지 url 정보를 담고 있음
        val savedRequest = requestCache.getRequest(request, response)
        if (savedRequest != null) {
            val targetUrl = savedRequest.redirectUrl
            // 필드로 따로 선언하는 것이 아닌 AbstractAuthenticationTargetUrlRequestHandler 에 있음
            redirectStrategy.sendRedirect(request, response, targetUrl)
        } else {
            redirectStrategy.sendRedirect(request, response, defaultTargetUrl)
        }
    }
}