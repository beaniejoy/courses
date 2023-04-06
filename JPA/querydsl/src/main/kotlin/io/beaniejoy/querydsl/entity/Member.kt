package io.beaniejoy.querydsl.entity

import jakarta.persistence.*

@Entity
class Member protected constructor(
    username: String?,
    age: Int
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L

    var username: String? = username
        protected set

    var age: Int = age
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    var team: Team? = null
        protected set

    companion object {
        fun createMember(username: String?, age: Int): Member {
            return Member(username = username, age = age)
        }
    }

    fun changeTeam(team: Team) {
        this.team = team
        team.members.add(this)
    }

    fun updateName(username: String) {
        this.username = username
    }

    override fun toString(): String {
        return "Member(id=$id, username='$username', age=$age)"
    }
}