package com.magalu.favoriteproducts.application.security

import com.magalu.favoriteproducts.application.configuration.JwtProperties
import com.magalu.favoriteproducts.domain.model.Client
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date

@Component
class JwtHandler(
    private val props: JwtProperties,
) {
    private val key = Keys.hmacShaKeyFor(props.secret.toByteArray(StandardCharsets.UTF_8))

    fun generateToken(client: Client): String {
        val now = Instant.now()
        val exp = Date.from(now.plus(props.expirationMinutes, ChronoUnit.MINUTES))
        return Jwts
            .builder()
            .setSubject(client.externalId.toString())
            .setIssuer(props.issuer)
            .setIssuedAt(Date.from(now))
            .setExpiration(exp)
            .claim("email", client.email)
            .claim("name", client.name)
            .claim("roles", "CLIENT")
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    fun validateAndParse(token: String): Jws<Claims> =
        Jwts
            .parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
}
