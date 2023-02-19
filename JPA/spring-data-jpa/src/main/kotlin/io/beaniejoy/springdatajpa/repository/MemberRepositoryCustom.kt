package io.beaniejoy.springdatajpa.repository

import io.beaniejoy.springdatajpa.entity.Member

interface MemberRepositoryCustom {
    fun findMemberCustom(): List<Member>
}