package com.example.demo.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.password.DelegatingPasswordEncoder
import org.springframework.security.crypto.password.NoOpPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(value = [AuthApiUser::class])
class WebSecurityConfig(private val apiUser: AuthApiUser) : WebSecurityConfigurerAdapter() {

    companion object {
        private const val NOOP_PASSWORD_ENCODER_ID = "noop"
        private const val NOOP_PASSWORD_ENCODER = "{noop}"
        private const val API_ROLE = "API"
        private val AUTH_WHITELIST = arrayOf(
            "/actuator/**",
            "/api-docs/**"
        )
    }

    @Bean
    fun delegatingPasswordEncoder(): PasswordEncoder? {
        val noOpEncoder = NoOpPasswordEncoder.getInstance()
        val dpe = DelegatingPasswordEncoder(NOOP_PASSWORD_ENCODER_ID, mapOf(NOOP_PASSWORD_ENCODER_ID to noOpEncoder))
        dpe.setDefaultPasswordEncoderForMatches(noOpEncoder)
        return dpe
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.inMemoryAuthentication()
            .withUser(apiUser.user).password(NOOP_PASSWORD_ENCODER + apiUser.password).roles(API_ROLE)
    }

    override fun configure(http: HttpSecurity) {
        http.authorizeRequests()
            .antMatchers(*AUTH_WHITELIST).permitAll()
            .antMatchers("/**").hasRole(API_ROLE)
            .and()
            .httpBasic()
            .and()
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    }
}

@ConstructorBinding
@ConfigurationProperties(prefix = "auth.basic.api")
data class AuthApiUser(val user: String, val password: String)
