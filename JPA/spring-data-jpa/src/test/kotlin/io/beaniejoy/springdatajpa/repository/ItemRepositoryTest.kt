package io.beaniejoy.springdatajpa.repository

import io.beaniejoy.springdatajpa.entity.Item
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ItemRepositoryTest {
    @Autowired
    lateinit var itemRepository: ItemRepository

    @Test
    fun save() {
        itemRepository.save(Item("A"))
    }
}