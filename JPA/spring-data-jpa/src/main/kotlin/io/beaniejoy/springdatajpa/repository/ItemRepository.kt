package io.beaniejoy.springdatajpa.repository

import io.beaniejoy.springdatajpa.entity.Item
import org.springframework.data.jpa.repository.JpaRepository

interface ItemRepository: JpaRepository<Item, String> {
}