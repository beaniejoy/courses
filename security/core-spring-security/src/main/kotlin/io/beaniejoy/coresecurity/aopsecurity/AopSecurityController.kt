package io.beaniejoy.coresecurity.aopsecurity

import io.beaniejoy.coresecurity.domain.dto.AccountDto
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import java.security.Principal

@Controller
class AopSecurityController {
    @GetMapping("/preAuthorize")
    @PreAuthorize("hasRole('ROLE_USER') and #account.username == principal.username")
    fun preAuthorize(account: AccountDto, model: Model, principal: Principal?): String {
        model["method"] = "Success @PreAuthorize"

        return "aop/method"
    }
}