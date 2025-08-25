package com.magalu.favoriteproducts.domain

import com.magalu.favoriteproducts.domain.model.Client
import java.time.OffsetDateTime
import java.util.UUID

interface ClientsPort {
    fun create(client: Client): Client

    fun update(
        client: Client,
        isUpdatedWithFavoriteProducts: Boolean = false,
    ): Client

    fun delete(externalId: UUID): String

    fun search(externalId: UUID): Client?

    fun searchWithFavoriteProducts(externalId: UUID): Client?

    fun updateByOnly(
        externalId: UUID,
        name: String,
        email: String,
        updatedAt: OffsetDateTime,
    ): Client

    fun existsByEmail(email: String): Boolean
}
