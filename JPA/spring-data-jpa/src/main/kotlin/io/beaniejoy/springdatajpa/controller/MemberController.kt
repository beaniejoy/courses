package io.beaniejoy.springdatajpa.controller

import io.beaniejoy.springdatajpa.dto.MemberDto
import io.beaniejoy.springdatajpa.entity.Member
import io.beaniejoy.springdatajpa.repository.MemberRepository
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class MemberController(
    private val memberRepository: MemberRepository
) {
    @GetMapping("/members/{id}")
    fun findMember(@PathVariable("id") id: Long): String {
        val member = memberRepository.findByIdOrNull(id)
        return member!!.username
    }

    // 도메인 클래스 컨버터
    // (Spring Data JPA가 알아서 Member 주입, 권장 X)
    @GetMapping("/members2/{id}")
    fun findMember2(@PathVariable("id") member: Member?): String {
        return member!!.username
    }

    @GetMapping("/members")
    fun list(@PageableDefault(page = 1, size = 5) pageable: Pageable, @Qualifier("member") memberPageable: Pageable): Page<MemberDto> {
        return memberRepository.findAll(pageable).map { MemberDto(id = it.id, username = it.username, it.team?.name) }
    }

//    @PostConstruct
    fun init() {
        for (i in 1..100) {
            memberRepository.save(Member.createMember("user$i", i))
        }
    }
}