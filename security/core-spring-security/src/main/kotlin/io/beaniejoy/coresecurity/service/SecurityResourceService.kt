package io.beaniejoy.coresecurity.service

import io.beaniejoy.coresecurity.repository.ResourcesRepository
import mu.KLogging
import org.springframework.security.access.ConfigAttribute
import org.springframework.security.access.SecurityConfig
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatcher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SecurityResourceService(
    private val resourcesRepository: ResourcesRepository,
) {
    companion object: KLogging()

    @Transactional
    fun getResourceList(): MutableMap<RequestMatcher, MutableList<ConfigAttribute>> {
        val resourcesList = resourcesRepository.findAllResources()

        // TODO: LinkedHashMap<K, V> generic 알아보기
        val linkedHashMap: LinkedHashMap<RequestMatcher, MutableList<ConfigAttribute>> = LinkedHashMap(
            resourcesList.associate {
                AntPathRequestMatcher(it.resourceName) to it.roleSet.map { role -> SecurityConfig(role.roleName) }
                    .toMutableList()
            }
        )

        logger.info { "resourceList $linkedHashMap" }

        return linkedHashMap
    }
}