package io.beaniejoy.springdatajpa.dto

import io.beaniejoy.springdatajpa.entity.Member

data class MemberDto(
    val id: Long,
    val username: String,
    val teamName: String?,
) {
    companion object {
        fun of(member: Member): MemberDto {
            return MemberDto(
                id = member.id,
                username = member.username,
                teamName = member.team?.name
            )
        }
    }
}