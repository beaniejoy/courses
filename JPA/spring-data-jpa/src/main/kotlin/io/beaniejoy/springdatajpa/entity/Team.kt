package io.beaniejoy.springdatajpa.entity

import jakarta.persistence.*

@Entity
class Team protected constructor(name: String) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L

    var name: String = name
        protected set

    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY)
    val members: MutableList<Member> = mutableListOf()

    companion object {
        fun createTeam(name: String): Team {
            return Team(name)
        }
    }
}