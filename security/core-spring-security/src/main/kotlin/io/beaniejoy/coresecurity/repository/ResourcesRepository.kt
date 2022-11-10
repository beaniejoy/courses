package io.beaniejoy.coresecurity.repository

import io.beaniejoy.coresecurity.domain.entity.Resources
import org.springframework.data.jpa.repository.JpaRepository

interface ResourcesRepository : JpaRepository<Resources, Long> {
}