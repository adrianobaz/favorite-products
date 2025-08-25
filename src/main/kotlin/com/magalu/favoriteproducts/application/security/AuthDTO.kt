package com.magalu.favoriteproducts.application.security

data class TokenRequest(
    val externalId: String,
)

data class JwtResponse(
    val token: String,
    val expiresInSeconds: Long,
)
