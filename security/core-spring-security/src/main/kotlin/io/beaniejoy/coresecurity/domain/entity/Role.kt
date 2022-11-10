package io.beaniejoy.coresecurity.domain.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity
@Table(name = "ROLE")
class Role protected constructor(
    roleName: String? = null,
    roleDesc: String? = null
) {
    @Id
    @GeneratedValue
    @Column(name = "role_id")
    val id: Long = 0L

    @Column(name = "role_name")
    var roleName: String? = roleName
        protected set

    @Column(name = "role_desc")
    var roleDesc: String? = roleDesc
        protected set

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "roleSet")
    @OrderBy("ordernum desc")
    val resourcesSet: MutableSet<Resources> = LinkedHashSet()

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "userRoles")
    val accounts: MutableSet<Account> = HashSet()

    companion object {
        fun createRole(
            roleName: String,
            roleDesc: String
        ): Role {
            return Role(
                roleName = roleName,
                roleDesc = roleDesc
            )
        }

        fun empty(): Role {
            return Role()
        }
    }
}