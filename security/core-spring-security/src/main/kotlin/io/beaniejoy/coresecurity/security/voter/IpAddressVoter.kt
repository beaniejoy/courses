package io.beaniejoy.coresecurity.security.voter

import io.beaniejoy.coresecurity.service.SecurityResourceService
import org.springframework.security.access.AccessDecisionVoter
import org.springframework.security.access.AccessDecisionVoter.ACCESS_ABSTAIN
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.access.ConfigAttribute
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.WebAuthenticationDetails

class IpAddressVoter(
    private val securityResourceService: SecurityResourceService
): AccessDecisionVoter<Any> {
    override fun supports(attribute: ConfigAttribute?): Boolean {
        return true
    }

    override fun supports(clazz: Class<*>?): Boolean {
        return true
    }

    override fun vote(
        authentication: Authentication?,
        `object`: Any?,
        attributes: MutableCollection<ConfigAttribute>?,
    ): Int {
        val details = authentication!!.details as WebAuthenticationDetails
        val remoteAddress = details.remoteAddress

        if (securityResourceService.getAccessIpList().contains(remoteAddress).not()) {
            throw AccessDeniedException("Invalid IpAddress")
        }

        // 통과되면 이후 본래의 인가 처리로 넘김(추가 심의 진행)
        return ACCESS_ABSTAIN

    }
}