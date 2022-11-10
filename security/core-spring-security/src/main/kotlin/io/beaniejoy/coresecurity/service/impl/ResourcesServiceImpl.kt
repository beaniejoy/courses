package io.beaniejoy.coresecurity.service.impl

import io.beaniejoy.coresecurity.domain.entity.Resources
import io.beaniejoy.coresecurity.repository.ResourcesRepository
import io.beaniejoy.coresecurity.service.ResourcesService
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ResourcesServiceImpl(
    private val resourcesRepository: ResourcesRepository,
) : ResourcesService {
    override fun getResources(id: Long): Resources {
        return resourcesRepository.findByIdOrNull(id) ?: Resources.empty()
    }

    override fun getResources(): List<Resources> {
        return resourcesRepository.findAll(Sort.by(Sort.Order.asc("orderNum")))
    }

    override fun createResources(resources: Resources) {
        resourcesRepository.save(resources)
    }

    override fun deleteResources(id: Long) {
        resourcesRepository.deleteById(id)
    }
}