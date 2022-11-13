package io.beaniejoy.coresecurity.security.metadatasource

import org.springframework.security.access.ConfigAttribute
import org.springframework.security.web.FilterInvocation
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource
import org.springframework.security.web.util.matcher.RequestMatcher

class UrlFilterInvocationSecurityMetadataSource(
    private val requestMap: MutableMap<RequestMatcher, MutableList<ConfigAttribute>> = linkedMapOf()
): FilterInvocationSecurityMetadataSource {
    override fun getAttributes(`object`: Any): MutableCollection<ConfigAttribute>? {
        val request = (`object` as FilterInvocation).request

        requestMap.entries.forEach { (key, value) ->
            if (key.matches(request)) {
                return value
            }
        }

        return null
    }

    override fun getAllConfigAttributes(): MutableCollection<ConfigAttribute> {
        return this.requestMap.values.flatten().toMutableList()
    }

    override fun supports(clazz: Class<*>): Boolean {
        return FilterInvocation::class.java.isAssignableFrom(clazz)
    }
}