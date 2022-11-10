package io.beaniejoy.coresecurity.service

import io.beaniejoy.coresecurity.domain.entity.Resources

interface ResourcesService {
    fun getResources(id: Long): Resources

    fun getResources(): List<Resources>

    fun createResources(resources: Resources)

    fun deleteResources(id: Long)
}