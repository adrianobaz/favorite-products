package com.magalu.favoriteproducts.infrastructure.fakestore.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.magalu.favoriteproducts.domain.model.Product
import com.magalu.favoriteproducts.domain.model.Rating
import java.math.BigDecimal
import java.time.OffsetDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
data class FakeStoreProductResponse(
    val id: Long,
    val title: String,
    val price: BigDecimal,
    val image: String,
    val rating: FakeStoreRatingResponse? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class FakeStoreRatingResponse(
    val rate: Double,
    val count: Int,
)

fun FakeStoreProductResponse.toDomain() =
    Product(
        id,
        title,
        image,
        price,
        rating?.toDomain(),
        OffsetDateTime.now(),
        OffsetDateTime.now(),
    )

fun FakeStoreRatingResponse.toDomain() = Rating(rate, count)
