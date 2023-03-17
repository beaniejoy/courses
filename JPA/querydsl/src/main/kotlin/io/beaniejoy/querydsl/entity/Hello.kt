package io.beaniejoy.querydsl.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id

@Entity
class Hello {
    @Id
    @GeneratedValue
    var id: Long = 0L
        protected set
}