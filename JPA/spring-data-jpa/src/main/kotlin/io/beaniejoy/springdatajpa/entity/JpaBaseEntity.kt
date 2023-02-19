package io.beaniejoy.springdatajpa.entity

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import java.time.LocalDateTime

@MappedSuperclass
class JpaBaseEntity {
    @Column(updatable = false)
    lateinit var createdDate: LocalDateTime
        protected set

    lateinit var updatedDate: LocalDateTime
        protected set

    // 표준 JPA 지원
    @PrePersist
    fun prePersist() {
        val now = LocalDateTime.now()
        createdDate = now
        updatedDate = now
    }

    @PreUpdate
    fun preUpdate() {
        updatedDate = LocalDateTime.now()
    }
}