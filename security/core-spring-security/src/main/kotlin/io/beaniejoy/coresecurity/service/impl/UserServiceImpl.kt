package io.beaniejoy.coresecurity.service.impl

import io.beaniejoy.coresecurity.domain.Account
import io.beaniejoy.coresecurity.repository.UserRepository
import io.beaniejoy.coresecurity.service.UserService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service("userService")
class UserServiceImpl(
    private val userRepository: UserRepository
): UserService {

    @Transactional
    override fun createUser(account: Account) {
        userRepository.save(account)
    }
}