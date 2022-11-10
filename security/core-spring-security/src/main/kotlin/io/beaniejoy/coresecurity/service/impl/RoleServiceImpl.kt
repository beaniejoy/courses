package io.beaniejoy.coresecurity.service.impl

import io.beaniejoy.coresecurity.domain.entity.Role
import io.beaniejoy.coresecurity.repository.RoleRepository
import io.beaniejoy.coresecurity.service.RoleService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class RoleServiceImpl(
    private val roleRepository: RoleRepository,
) : RoleService {
    override fun getRole(id: Long): Role {
        return roleRepository.findByIdOrNull(id) ?: Role.empty()
    }

    override fun getRoles(): List<Role> {
        return roleRepository.findAll()
    }

    override fun createRole(role: Role) {
        roleRepository.save(role)
    }

    override fun deleteRole(id: Long) {
        roleRepository.deleteById(id)
    }
}