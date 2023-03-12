package io.beaniejoy.springdatajpa.entity

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@EntityListeners(AuditingEntityListener::class)
@MappedSuperclass
class BaseTimeEntity protected constructor() {
    @CreatedDate
    @Column(updatable = false)
    var createdDate: LocalDateTime? = null
        protected set

    @LastModifiedDate
    var lastModifiedDate: LocalDateTime? = null
        protected set
}