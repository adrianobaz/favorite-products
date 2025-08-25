package com.magalu.favoriteproducts.domain.model

import java.time.OffsetDateTime
import java.util.UUID

data class Client(
    val id: Long? = null,
    val externalId: UUID = UUID.randomUUID(),
    val name: String,
    val email: String,
    val favoriteProducts: Set<Product>? = null,
    val createdAt: OffsetDateTime? = null,
    val updatedAt: OffsetDateTime? = null,
)
