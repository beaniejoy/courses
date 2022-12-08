package io.beaniejoy.coresecurity.repository

import io.beaniejoy.coresecurity.domain.entity.AccessIp
import org.springframework.data.jpa.repository.JpaRepository

interface AccessIpRepository: JpaRepository<AccessIp, Long> {
    fun findByIpAddress(ipAddress: String)
}