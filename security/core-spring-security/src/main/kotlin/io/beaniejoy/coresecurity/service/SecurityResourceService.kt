package io.beaniejoy.coresecurity.service

import io.beaniejoy.coresecurity.domain.entity.Resources
import io.beaniejoy.coresecurity.repository.ResourcesRepository
import io.beaniejoy.coresecurity.util.Transaction
import mu.KLogging
import org.hibernate.Hibernate
import org.springframework.security.access.ConfigAttribute
import org.springframework.security.access.SecurityConfig
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatcher
import org.springframework.stereotype.Service
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.AbstractPlatformTransactionManager

@Service
@Transactional
class SecurityResourceService(
    private val resourcesRepository: ResourcesRepository
) {
    companion object : KLogging()

    // FIXME 수동으로 tx 시작 끝 지정하고 해보자
    // 의심되는 것은 Service 자체가 proxy로 안먹어서 발생하는 문제 같다.
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