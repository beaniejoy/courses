package io.brick.jpabook.jpashop.service.query

import io.brick.jpabook.jpashop.domain.api.OrderApiController
import io.brick.jpabook.jpashop.domain.api.OrderDto
import io.brick.jpabook.jpashop.repository.OrderRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class OrderQueryService(
    private val orderRepository: OrderRepository
) {
    /**
     * OSIV 비활성화에 따른 연관관계 Entity 조회
     * 지연 로딩 함께 조회(Tx 새로 묶어서)
     */
    fun getAllWithItem(): List<OrderDto> {
        return orderRepository.findAllWithItem().map {
            OrderApiController.logger.info { "order ref = $it / id = ${it.id}" }
            OrderDto.of(it)
        }
    }

}