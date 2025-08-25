package com.magalu.favoriteproducts.domain.model

import java.math.BigDecimal
import java.time.OffsetDateTime

data class Product(
    val id: Long,
    val title: String,
    val image: String,
    val price: BigDecimal,
    val rating: Rating? = null,
    val createdAt: OffsetDateTime? = null,
    val updatedAt: OffsetDateTime? = null,
)

data class Rating(
    val rate: Double,
    val count: Int,
)
