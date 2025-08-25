package com.magalu.favoriteproducts.application.configuration

import com.magalu.favoriteproducts.application.security.JwtAuthFilter
import com.magalu.favoriteproducts.application.security.JwtHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableMethodSecurity
class SecurityConfig(
    private val jwtHandler: JwtHandler,
) {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/auth/**")
                    .permitAll()
                    .requestMatchers(HttpMethod.POST, "/v1/clients")
                    .permitAll()
                    .requestMatchers("/admin/**")
                    .hasRole("ADMIN")
                    .anyRequest()
                    .authenticated()
            }.addFilterBefore(JwtAuthFilter(jwtHandler), UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }
}
