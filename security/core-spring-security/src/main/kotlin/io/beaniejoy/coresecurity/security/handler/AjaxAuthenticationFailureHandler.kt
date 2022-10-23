package io.beaniejoy.coresecurity.security.handler

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.InsufficientAuthenticationException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class AjaxAuthenticationFailureHandler: AuthenticationFailureHandler {
    private val objectMapper = jacksonObjectMapper()

    override fun onAuthenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException,
    ) {
        response.status = HttpStatus.UNAUTHORIZED.value() // 인증 실패
        response.contentType = MediaType.APPLICATION_JSON_VALUE

        val errorMessage = when (exception) {
            is BadCredentialsException -> "Invalid Username or Password"
            is InsufficientAuthenticationException -> "Invalid Secret Key"
            else -> "default error message"
        }

        objectMapper.writeValue(response.writer, errorMessage)
    }
}