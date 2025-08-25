package com.magalu.favoriteproducts.domain.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.math.BigDecimal
import java.time.OffsetDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
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
