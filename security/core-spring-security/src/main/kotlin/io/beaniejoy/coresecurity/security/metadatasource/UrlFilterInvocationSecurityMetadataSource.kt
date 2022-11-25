package io.beaniejoy.coresecurity.security.metadatasource

import io.beaniejoy.coresecurity.service.SecurityResourceService
import mu.KLogging
import org.springframework.security.access.ConfigAttribute
import org.springframework.security.web.FilterInvocation
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatcher

class UrlFilterInvocationSecurityMetadataSource(
    private val requestMap: MutableMap<RequestMatcher, MutableList<ConfigAttribute>> = linkedMapOf(),
    private val securityResourceService: SecurityResourceService
): FilterInvocationSecurityMetadataSource {
    companion object: KLogging()

    private var permitAllResources: Array<RequestMatcher> = arrayOf()

    fun setPermitAllResources(permitAllResources: Array<String>) {
        this.permitAllResources = permitAllResources
            .map { AntPathRequestMatcher(it) }
            .toTypedArray()
    }

    override fun getAttributes(`object`: Any): MutableCollection<ConfigAttribute>? {
        val request = (`object` as FilterInvocation).request

        // permitAll 조건 선체크
        this.permitAllResources.forEach {
            if (it.matches(request)) {
                return null
            }
        }

        // 설정된 요청정보 - 권한정보 매핑 내용 체크
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