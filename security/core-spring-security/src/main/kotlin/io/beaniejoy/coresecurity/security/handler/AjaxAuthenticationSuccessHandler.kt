package io.beaniejoy.coresecurity.security.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.beaniejoy.coresecurity.domain.Account
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class AjaxAuthenticationSuccessHandler: AuthenticationSuccessHandler {
    private val objectMapper = jacksonObjectMapper()

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication,
    ) {
        val account = authentication.principal as Account

        response.status = HttpStatus.OK.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE

        // response body 부분에 데이터를 넣어서 응답
        objectMapper.writeValue(response.writer, account)
    }
}