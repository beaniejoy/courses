package io.beaniejoy.springdatajpa.entity

import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.domain.Persistable
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@EntityListeners(AuditingEntityListener::class)
class Item constructor(
    id: String?
): Persistable<String> {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id: String? = id

    @CreatedDate
    var createdDate: LocalDateTime? = null
        protected set

    override fun getId(): String? {
        return id
    }

    // save 할 때 persist, merge 기준 설정
    override fun isNew(): Boolean {
        return createdDate == null
    }
}