package io.beaniejoy.coresecurity.security.handler

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.beaniejoy.coresecurity.domain.entity.Account
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

    /**
     * [### 주의 ###]
     * 여기는 @Transactional 이미 끝난 상태
     * Account를 직접 ObjectMapper serializing 처리를 한다면 그 안에 Role 엔티티 getter 모두 활용
     * Role 엔티티에서 resourcesSet, accounts LAZY 프록시 기능 적용 불가 (에러 발생)
     */
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