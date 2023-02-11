package io.beaniejoy.springdatajpa.repository

import io.beaniejoy.springdatajpa.dto.MemberDto
import io.beaniejoy.springdatajpa.entity.Member
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface MemberRepository: JpaRepository<Member, Long> {
    fun findByUsernameAndAgeGreaterThan(username: String, age: Int): List<Member>

    // 전체조회랑 같다.
    fun findHelloBy(): List<Member>

    // NameQuery
    @Query(name = "Member.findByUsername")
    fun findByUsername(@Param("username") username: String): List<Member>

    @Query("select m from Member m where m.username = :username and m.age = :age")
    fun findUser(@Param("username") username: String, @Param("age") age: Int): List<Member>

    // value로 조회
    @Query("select m.username from Member m")
    fun findUsernameList(): List<String>

    // DTO로 조회
    @Query("select new io.beaniejoy.springdatajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    fun findMemberDto(): List<MemberDto>

    @Query("select m from Member m where m.username in :names")
    fun findByNames(@Param("names") names: List<String>): List<Member>

    fun findListByUsername(username: String): List<Member> // 컬렉션
    fun findMemberByUsername(username: String): Member? // 단건
    fun findOptionalByUsername(username: String): Optional<Member> // Optional wrapping
}