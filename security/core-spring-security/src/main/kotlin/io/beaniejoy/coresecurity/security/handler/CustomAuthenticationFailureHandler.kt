package io.beaniejoy.coresecurity.security.handler

import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.InsufficientAuthenticationException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

// it can be also applied with implementing AuthenticationFailureHandler interface
@Component
class CustomAuthenticationFailureHandler: SimpleUrlAuthenticationFailureHandler() {
    override fun onAuthenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException,
    ) {
        val errorMessage = when (exception) {
            is BadCredentialsException -> "Invalid Username or Password"
            is InsufficientAuthenticationException -> "Invalid Secret Key"
            else -> "default error message"
        }

        setDefaultFailureUrl("/login?error=true&exception=${errorMessage}")

        super.onAuthenticationFailure(request, response, exception)
    }
}