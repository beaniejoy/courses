package io.beaniejoy.coresecurity.domain.entity

import java.io.Serializable
import javax.persistence.*

@Entity
@Table(name = "RESOURCES")
class Resources protected constructor(
    resourceName: String? = null,
    httpMethod: String? = null,
    orderNum: Int? = null,
    resourceType: String? = null,
    roleSet: HashSet<Role> = hashSetOf()
) : Serializable {
    @Id
    @GeneratedValue
    @Column(name = "resource_id")
    val id: Long = 0L

    @Column(name = "resource_name")
    var resourceName: String? = resourceName
        protected set

    @Column(name = "http_method")
    var httpMethod: String? = httpMethod
        protected set

    @Column(name = "order_num")
    var orderNum: Int? = orderNum
        protected set

    @Column(name = "resource_type")
    var resourceType: String? = resourceType
        protected set

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "role_resources",
        joinColumns = [JoinColumn(name = "resource_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")]
    )
    val roleSet: MutableSet<Role> = roleSet

    companion object {
        fun createResources(
            resourceName: String,
            httpMethod: String,
            orderNum: Int,
            resourceType: String,
            roleSet: HashSet<Role>
        ): Resources {
            return Resources(
                resourceName = resourceName,
                httpMethod = httpMethod,
                orderNum = orderNum,
                resourceType = resourceType,
                roleSet = roleSet
            )
        }

        fun empty(): Resources {
            return Resources()
        }
    }
}