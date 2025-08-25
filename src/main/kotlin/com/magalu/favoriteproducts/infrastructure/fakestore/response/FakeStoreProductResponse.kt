package com.magalu.favoriteproducts.infrastructure.fakestore.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.magalu.favoriteproducts.domain.model.Product
import com.magalu.favoriteproducts.domain.model.Rating
import java.math.BigDecimal
import java.time.OffsetDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
data class FakeStoreProductResponse(
    @field:JsonProperty("id")
    val id: Long,
    @field:JsonProperty("title")
    val title: String,
    @field:JsonProperty("price")
    val price: BigDecimal,
    @field:JsonProperty("image")
    val image: String,
    val rating: FakeStoreRatingResponse? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class FakeStoreRatingResponse(
    @field:JsonProperty("rate")
    val rate: Double,
    @field:JsonProperty("count")
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
