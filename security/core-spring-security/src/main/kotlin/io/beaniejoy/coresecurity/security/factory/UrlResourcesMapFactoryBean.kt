package io.beaniejoy.coresecurity.security.factory

import io.beaniejoy.coresecurity.service.SecurityResourceService
import org.springframework.beans.factory.FactoryBean
import org.springframework.security.access.ConfigAttribute
import org.springframework.security.web.util.matcher.RequestMatcher
import org.springframework.stereotype.Component

@Component
class UrlResourcesMapFactoryBean(
    private val securityResourceService: SecurityResourceService
): FactoryBean<MutableMap<RequestMatcher, MutableList<ConfigAttribute>>> {
     private var resourceMap: MutableMap<RequestMatcher, MutableList<ConfigAttribute>>? = null

    override fun getObject(): MutableMap<RequestMatcher, MutableList<ConfigAttribute>> {
        if (resourceMap == null) {
            init()
        }

        return resourceMap!!
    }

    private fun init() {
        this.resourceMap = securityResourceService.getResourceList()
    }

    override fun getObjectType(): Class<*> {
        return MutableMap::class.java
    }
}