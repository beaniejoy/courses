package io.beaniejoy.coresecurity.security.config

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder

@SpringBootTest
internal class PasswordEncoderTest {
    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

    @Test
    fun test() {

        println(passwordEncoder.encode("1111"))
    }
}
