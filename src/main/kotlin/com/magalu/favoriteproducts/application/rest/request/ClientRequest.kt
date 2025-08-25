package com.magalu.favoriteproducts.application.rest.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class ClientRequest(
    @field:NotBlank
    val name: String,
    @field:Email
    @field:NotBlank
    val email: String,
)

data class ClientUpdateRequest(
    val name: String? = null,
    @field:Email
    val email: String? = null,
)
