package io.beaniejoy.springdatajpa.repository

interface MemberProjection {
    fun getId(): Long
    fun getUsername(): String
    fun getTeamName(): String
}