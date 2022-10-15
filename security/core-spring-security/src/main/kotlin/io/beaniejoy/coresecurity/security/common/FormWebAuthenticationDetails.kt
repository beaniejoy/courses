package io.beaniejoy.coresecurity.security.common

import org.springframework.security.web.authentication.WebAuthenticationDetails
import javax.servlet.http.HttpServletRequest

class FormWebAuthenticationDetails(
    request: HttpServletRequest
): WebAuthenticationDetails(request) {
    var secretKey: String?
        private set

    init {
        this.secretKey = request.getParameter("secret_key")
    }
}