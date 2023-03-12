package io.beaniejoy.springdatajpa.entity

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@EntityListeners(AuditingEntityListener::class)
@MappedSuperclass
class BaseEntity protected constructor() : BaseTimeEntity() {
    @CreatedBy
    @Column(updatable = false)
    var createdBy: String? = null
        protected set

    @LastModifiedBy
    var updatedBy: String? = null
        protected set
}