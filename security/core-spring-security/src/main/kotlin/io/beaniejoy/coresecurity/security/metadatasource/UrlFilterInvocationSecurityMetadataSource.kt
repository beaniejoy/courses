package io.beaniejoy.coresecurity.security.metadatasource

import io.beaniejoy.coresecurity.service.SecurityResourceService
import mu.KLogging
import org.springframework.security.access.ConfigAttribute
import org.springframework.security.web.FilterInvocation
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource
import org.springframework.security.web.util.matcher.RequestMatcher

class UrlFilterInvocationSecurityMetadataSource(
    private val requestMap: MutableMap<RequestMatcher, MutableList<ConfigAttribute>> = linkedMapOf(),
    private val securityResourceService: SecurityResourceService
): FilterInvocationSecurityMetadataSource {
    companion object: KLogging()

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

    fun reload() {
        val reloadedMap = securityResourceService.getResourceList()

        requestMap.clear()
        requestMap.apply {
            reloadedMap.forEach { (key, value) ->
                this[key] = value
            }
        }

        logger.info { "##### metadataSource requestMap reload complete!" }
    }
}