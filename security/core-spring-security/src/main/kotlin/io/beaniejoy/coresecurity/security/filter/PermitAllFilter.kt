package io.beaniejoy.coresecurity.security.filter

import org.springframework.security.access.intercept.InterceptorStatusToken
import org.springframework.security.web.FilterInvocation
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatcher

class PermitAllFilter(
    vararg permitAllResources: String,
) : FilterSecurityInterceptor() {
    private val permitAllRequestMatchers: MutableList<RequestMatcher> = mutableListOf()

    init {
        permitAllResources.forEach {
            this.permitAllRequestMatchers.add(AntPathRequestMatcher(it))
        }
    }

    // FIXME invoke 구현문제
    override fun beforeInvocation(any: Any?): InterceptorStatusToken? {
        val request = (any as FilterInvocation).request
        this.permitAllRequestMatchers
            .any { it.matches(request) }
            .also { isPermitAll ->
                if (isPermitAll) {
                    return null
                }
            }

        return super.beforeInvocation(any)
    }
}