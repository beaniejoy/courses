package io.beaniejoy.coresecurity.security.config

import io.beaniejoy.coresecurity.security.factory.UrlResourcesMapFactoryBean
import io.beaniejoy.coresecurity.security.handler.FormAccessDeniedHandler
import io.beaniejoy.coresecurity.security.handler.FormAuthenticationFailureHandler
import io.beaniejoy.coresecurity.security.handler.FormAuthenticationSuccessHandler
import io.beaniejoy.coresecurity.security.metadatasource.UrlFilterInvocationSecurityMetadataSource
import io.beaniejoy.coresecurity.security.voter.IpAddressVoter
import io.beaniejoy.coresecurity.service.RoleHierarchyService
import io.beaniejoy.coresecurity.service.SecurityResourceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.access.AccessDecisionManager
import org.springframework.security.access.AccessDecisionVoter
import org.springframework.security.access.hierarchicalroles.RoleHierarchy
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl
import org.springframework.security.access.vote.AffirmativeBased
import org.springframework.security.access.vote.RoleHierarchyVoter
import org.springframework.security.access.vote.RoleVoter
import org.springframework.security.authentication.AuthenticationDetailsSource
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor
import javax.servlet.http.HttpServletRequest

@Configuration
@Order(1)
class SecurityConfig {
    @Autowired
    lateinit var authenticationDetailsSource: AuthenticationDetailsSource<HttpServletRequest, *>

    @Autowired
    lateinit var formAuthenticationSuccessHandler: FormAuthenticationSuccessHandler

    @Autowired
    lateinit var formAuthenticationFailureHandler: FormAuthenticationFailureHandler

    @Autowired
    lateinit var authenticationConfiguration: AuthenticationConfiguration

    @Autowired
    lateinit var urlResourcesMapFactoryBean: UrlResourcesMapFactoryBean

    @Autowired
    lateinit var securityResourceService: SecurityResourceService

    @Autowired
    lateinit var roleHierarchyService: RoleHierarchyService

    private val permitAllResources: Array<String> = arrayOf("/", "/login")

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .authorizeRequests()
            .antMatchers(*permitAllResources).permitAll() // custom securityMetadataSource 적용하면 permitAll은 의미 없는 것이 아닌지
//            .antMatchers("/mypage").hasRole("USER")
//            .antMatchers("/messages").hasRole("MANAGER")
//            .antMatchers("/config").hasRole("ADMIN")
            .anyRequest().authenticated()

                // formLogin 기능이 필요 없을 듯
            .and()
            .formLogin()
            .loginPage("/login")
//            .loginProcessingUrl("/login_proc")
//            .authenticationDetailsSource(authenticationDetailsSource)
//            .defaultSuccessUrl("/")
//            .successHandler(formAuthenticationSuccessHandler)
//            .failureHandler(formAuthenticationFailureHandler)
//            .permitAll()

            .and()
            .exceptionHandling()
            .accessDeniedHandler(accessDeniedHandler())

            .and()
            .addFilterBefore(customFilterSecurityInterceptor(), FilterSecurityInterceptor::class.java)

            .build()
    }

    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer {
        return WebSecurityCustomizer { web ->
            web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations())
            web.ignoring().antMatchers("/error")
        }
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder()
    }

    @Bean
    fun accessDeniedHandler(): FormAccessDeniedHandler {
        return FormAccessDeniedHandler("/denied")
    }

    @Bean
    fun customFilterSecurityInterceptor(): FilterSecurityInterceptor {
        return FilterSecurityInterceptor().apply {
            this.securityMetadataSource = urlFilterInvocationSecurityMetadataSource()
            this.accessDecisionManager = affirmativeBased()
            this.authenticationManager = authenticationConfiguration.authenticationManager
        }
    }

    @Bean
    fun urlFilterInvocationSecurityMetadataSource(): UrlFilterInvocationSecurityMetadataSource {
        return UrlFilterInvocationSecurityMetadataSource(
            requestMap = urlResourcesMapFactoryBean.getObject(),
            securityResourceService = securityResourceService
        ).apply {
            this.setPermitAllResources(this@SecurityConfig.permitAllResources)
        }
    }

    @Bean
    fun affirmativeBased(): AccessDecisionManager {
        return AffirmativeBased(getAccessDecisionVoters())
    }

    private fun getAccessDecisionVoters(): MutableList<AccessDecisionVoter<*>> {
        return mutableListOf<AccessDecisionVoter<out Any>>().apply {
//            add(RoleVoter())
            add(IpAddressVoter(securityResourceService))
            add(roleVoter())    // apply role hierarchy setup
        }
    }

    @Bean
    fun roleVoter(): AccessDecisionVoter<out Any> {
        return RoleHierarchyVoter(roleHierarchy())
    }

    @Bean
    fun roleHierarchy(): RoleHierarchy {
        return RoleHierarchyImpl().apply {
            setHierarchy(roleHierarchyService.findAllHierarchy())
        }
    }
}