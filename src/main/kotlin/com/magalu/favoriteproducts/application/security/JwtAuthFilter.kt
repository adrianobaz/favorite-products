package com.magalu.favoriteproducts.application.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class JwtAuthFilter(
    private val jwtHandler: JwtHandler,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        req: HttpServletRequest,
        res: HttpServletResponse,
        chain: FilterChain,
    ) {
        val header = req.getHeader(HttpHeaders.AUTHORIZATION)
        if (!header.isNullOrBlank() && header.startsWith("Bearer ")) {
            val token = header.substring(7)
            try {
                val jws = jwtHandler.validateAndParse(token)
                val claims = jws.body
                val userId = claims.subject
                val roles =
                    (claims["roles"] as? String ?: "")
                        .split(",")
                        .mapNotNull { r -> r.trim().takeIf { it.isNotEmpty() }?.let { SimpleGrantedAuthority(it) } }

                val auth = UsernamePasswordAuthenticationToken(userId, null, roles)
                SecurityContextHolder.getContext().authentication = auth
            } catch (ex: Exception) {
                logger.warn("JWT invalid: ${ex.message}")
            }
        }
        chain.doFilter(req, res)
    }
}
