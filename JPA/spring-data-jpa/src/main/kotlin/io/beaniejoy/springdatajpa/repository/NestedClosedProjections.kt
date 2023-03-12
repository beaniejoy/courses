package io.beaniejoy.springdatajpa.repository

interface NestedClosedProjections {
    fun getUsername(): String
    fun getTeam(): TeamInfo

    interface TeamInfo {
        fun getName(): String
    }
}