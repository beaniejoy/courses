package io.beaniejoy.coresecurity.service.impl

import io.beaniejoy.coresecurity.repository.RoleHierarchyRepository
import io.beaniejoy.coresecurity.service.RoleHierarchyService
import mu.KLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RoleHierarchyServiceImpl(
    private val roleHierarchyRepository: RoleHierarchyRepository
) : RoleHierarchyService {
    companion object: KLogging()

    @Transactional
    override fun findAllHierarchy(): String {
        val roleHierarchies = roleHierarchyRepository.findAll()

        return roleHierarchies
            .filter { it.parentName != null }
            .joinToString("\n") { "${it.parentName!!.childName} > ${it.childName}" }
            .also {
                logger.info { "result hierarchy: $it" }
            }
    }
}