package io.beaniejoy.coresecurity.domain.entity

import java.io.Serializable
import javax.persistence.*

@Entity
@Table(name = "ROLE_HIERARCHY")
class RoleHierarchy(
    childName: String
) : Serializable {
    @Id
    @GeneratedValue
    val id: Long = 0L

    @Column(name = "child_name")
    var childName: String = childName
        protected set

    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinColumn(name = "parent_name", referencedColumnName = "child_name")
    val parentName: RoleHierarchy? = null

    @OneToMany(mappedBy = "parentName", cascade = [CascadeType.ALL])
    val roleHierarchy: MutableSet<RoleHierarchy> = mutableSetOf()
}