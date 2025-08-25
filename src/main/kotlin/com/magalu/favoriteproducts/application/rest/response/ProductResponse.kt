package com.magalu.favoriteproducts.application.rest.response

import java.math.BigDecimal

data class ProductResponse(
    val id: Long,
    val title: String,
    val image: String,
    val price: BigDecimal,
    val rating: RatingResponse? = null,
)

data class RatingResponse(
    val rate: Double,
    val count: Int,
)
