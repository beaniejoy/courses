package io.beaniejoy.coresecurity.service.impl

import io.beaniejoy.coresecurity.domain.constant.RoleType
import io.beaniejoy.coresecurity.domain.dto.AccountDto
import io.beaniejoy.coresecurity.domain.entity.Account
import io.beaniejoy.coresecurity.repository.RoleRepository
import io.beaniejoy.coresecurity.repository.UserRepository
import io.beaniejoy.coresecurity.service.UserService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service("userService")
@Transactional
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val passwordEncoder: PasswordEncoder,
) : UserService {

    override fun createUser(account: Account) {
        val role = roleRepository.findByRoleName(RoleType.ROLE_USER.name)
            ?: throw RuntimeException("${RoleType.ROLE_USER} not existed")

        account.addRoles(hashSetOf(role))

        userRepository.save(account)
    }

    override fun modifyUser(accountDto: AccountDto) {
        val account = Account.createAccount(
            username = accountDto.username,
            password = passwordEncoder.encode(accountDto.password),
            email = accountDto.email!!,
            age = accountDto.age!!
        )

        account.modifyUserRoles(
            accountDto.roles.map {
                roleRepository.findByRoleName(it)
                    ?: throw RuntimeException("${RoleType.ROLE_USER} not existed")
            }.toHashSet()
        )

        userRepository.save(account)
    }

    override fun getUser(id: Long): AccountDto {
        val account = userRepository.findByIdOrNull(id) ?: Account.empty()

        return AccountDto.of(account)
    }

    override fun getUsers(): List<Account> {
        return userRepository.findAll()
    }

    override fun deleteUser(id: Long) {
        userRepository.deleteById(id)
    }
}