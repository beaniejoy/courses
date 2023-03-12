package io.beaniejoy.springdatajpa.repository

import io.beaniejoy.springdatajpa.entity.Member
import io.beaniejoy.springdatajpa.entity.Team
import jakarta.persistence.criteria.*
import org.springframework.data.jpa.domain.Specification

class MemberSpec {
    companion object {
        fun teamName(teamName: String): Specification<Member> {
            return Specification<Member> { root, query, builder ->
                if (teamName.isEmpty()) {
                    return@Specification null
                }

                val t = root.join<Member, Team>("team", JoinType.INNER)
                return@Specification builder.equal(t.get<String>("name"), teamName)
            }
        }

        fun username(username: String): Specification<Member> {
            return Specification<Member> { root, query, builder ->
                builder.equal(root.get<String>("username"), username)
            }
        }
    }
}