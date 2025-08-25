package com.magalu.favoriteproducts.domain

import com.magalu.favoriteproducts.domain.model.Client
import java.util.UUID

interface ClientsTokenPort {
    fun setEmail(email: String): Boolean

    fun removeEmail(email: String): Boolean

    fun emailAlreadyExists(email: String): Boolean

    fun createMagicToken(
        externalId: UUID,
        ttlMinutes: Long,
    ): String

    fun consumeMagicToken(token: String): Client?
}
