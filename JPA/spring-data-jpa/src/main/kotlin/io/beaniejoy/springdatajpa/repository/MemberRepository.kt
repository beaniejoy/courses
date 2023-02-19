package io.beaniejoy.springdatajpa.repository

import io.beaniejoy.springdatajpa.dto.MemberDto
import io.beaniejoy.springdatajpa.entity.Member
import jakarta.persistence.LockModeType
import jakarta.persistence.QueryHint
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.QueryHints
import org.springframework.data.repository.query.Param
import java.util.*

interface MemberRepository: JpaRepository<Member, Long>, MemberRepositoryCustom {
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

    // paging
    fun findByAge(age: Int, pageable: Pageable): Page<Member>
    fun findWithSliceByAge(age: Int, pageable: Pageable): Slice<Member>
    fun findWithListByAge(age: Int, pageable: Pageable): List<Member>
    @Query(value = "select m from Member m left join m.team t", countQuery = "select count(m) from Member m")
    fun findWithCountQueryByAge(age: Int, pageable: Pageable): List<Member>

    // JPQL update 쿼리 실행할 때 @Modifying 꼭 있어야한다.
    // 일종의 'executeUpdate()'와 같은 역할
    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    fun bulkAgePlus(@Param("age") age: Int): Int

    // fetch join 이용한 N+1 문제 해결
    @Query("select m from Member m left join fetch m.team")
    fun findMemberFetchJoin(): List<Member>
    // EntityGraph 이용한 N+1 문제 해결
    @EntityGraph(attributePaths = ["team"])
    override fun findAll(): List<Member>
    @EntityGraph(attributePaths = ["team"])
    @Query("select m from Member m")
    fun findMemberEntityGraph(): List<Member>

//    @EntityGraph(attributePaths = ["team"])
    @EntityGraph("Member.all") // NamedEntityGraph 적용
    fun findEntityGraphByUsername(@Param("username") username: String): List<Member> // find...ByUsername ...에 아무거나 상관없음

    // JPA hints
    @QueryHints(QueryHint(name = "org.hibernate.readOnly", value = "true")) // readOnly
    fun findReadOnlyByUsername(username: String): Member?

    // Lock
    // select for update
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    fun findLockByUsername(username: String): List<Member>
}