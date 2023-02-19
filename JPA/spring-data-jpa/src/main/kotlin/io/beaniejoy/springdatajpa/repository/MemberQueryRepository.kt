package io.beaniejoy.springdatajpa.repository

import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository

/**
 * 통계성 쿼리, 화면에 맞춘 복잡한 쿼리를 관리하는 repository
 * 꼭 EntityManager가 아니어도 JdbcTemplate 등 다른 기술로도 적용 가능
 * (Spring Data JPA와 별개로 움직인다.)
 */
@Repository
class MemberQueryRepository(
    private val em: EntityManager
) {
    //...
}