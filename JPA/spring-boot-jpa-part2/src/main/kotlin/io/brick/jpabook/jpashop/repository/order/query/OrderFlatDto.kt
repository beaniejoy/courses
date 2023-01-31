package io.brick.jpabook.jpashop.repository.order.query

import io.brick.jpabook.jpashop.domain.Address
import io.brick.jpabook.jpashop.domain.OrderStatus
import java.time.LocalDateTime

// 연관관계 있는 entity 정보들 모두 하나의 DTO로 가져오기
data class OrderFlatDto(
    // Order
    val orderId: Long,
    val name: String,
    val orderDate: LocalDateTime,
    val orderStatus: OrderStatus,
    val address: Address,

    // orderItem
    val itemName: String,
    val orderPrice: Int,
    val count: Int
)