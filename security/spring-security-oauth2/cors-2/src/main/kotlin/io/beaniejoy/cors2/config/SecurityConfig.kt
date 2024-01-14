package io.beaniejoy.cors2.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class SecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .authorizeHttpRequests {
                it.anyRequest().authenticated()
            }
            .cors {
                it.configurationSource(corsConfigurationSource())
            }
            .build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        // 아래의 정책과 클라이언트 요청 헤더 내용하고 맞지 않으면 브라우저에서 걸러내고
        // CORS 위반 에러를 반환해준다.
        val corsConfiguration = CorsConfiguration().apply {
            this.addAllowedOrigin("*")  // *: 모든 origin domain에 대해 허용
            this.addAllowedMethod("*")
            this.addAllowedHeader("*")
//            this.allowCredentials = true
            this.maxAge = 3_600L // 이 부분은 어떤 것인지 아직 이해 못함
        }

        return UrlBasedCorsConfigurationSource().apply {
            this.registerCorsConfiguration("/**", corsConfiguration)
        }
    }
}