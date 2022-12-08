package io.beaniejoy.coresecurity.domain.entity

import javax.persistence.*

@Entity
@Table(name = "ACCESS_IP")
class AccessIp(
    ipAddress: String
) {
    @Id
    @GeneratedValue
    @Column(name = "ip_id", unique = true, nullable = false)
    val id: Long = 0L

    @Column(name = "ip_address", nullable = false)
    var ipAddress: String = ipAddress
        protected set
}