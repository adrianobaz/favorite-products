package com.magalu.favoriteproducts.application.security

import com.magalu.favoriteproducts.application.rest.ClientFavoriteProductsController.Companion.UUIDV4_REGEX
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class TokenRequest(
    @field:NotBlank
    @field:Pattern(
        regexp = UUIDV4_REGEX,
        message = "Invalid UUIDv4 format",
    )
    val externalId: String,
)

data class JwtResponse(
    val token: String,
    val expiresInSeconds: Long,
)
