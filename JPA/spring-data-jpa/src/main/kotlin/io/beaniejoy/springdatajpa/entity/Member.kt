package io.beaniejoy.springdatajpa.entity

import jakarta.persistence.*

@Entity
@NamedQuery(
    name = "Member.findByUsername",
    query = "select m from Member m where m.username = :username"
)
@NamedEntityGraph(name = "Member.all", attributeNodes = [NamedAttributeNode("team")])
class Member protected constructor(
    username: String,
    age: Int
) : BaseEntity() {
    @Id
    @GeneratedValue
    val id: Long = 0L

    var username: String = username
        protected set

    var age: Int = age
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    var team: Team? = null
        protected set

    companion object {
        fun createMember(username: String, age: Int): Member {
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
}