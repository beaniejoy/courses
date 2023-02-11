package io.beaniejoy.springdatajpa.repository

import io.beaniejoy.springdatajpa.entity.Team
import org.springframework.data.jpa.repository.JpaRepository

interface TeamRepository : JpaRepository<Team, Long> {
}