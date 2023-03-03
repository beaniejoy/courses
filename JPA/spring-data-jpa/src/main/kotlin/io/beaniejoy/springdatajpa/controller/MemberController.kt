package io.beaniejoy.springdatajpa.controller

import io.beaniejoy.springdatajpa.entity.Member
import io.beaniejoy.springdatajpa.repository.MemberRepository
import jakarta.annotation.PostConstruct
import org.springframework.data.repository.findByIdOrNull
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

    @PostConstruct
    fun init() {
        memberRepository.save(Member.createMember("userA", 10))
    }
}