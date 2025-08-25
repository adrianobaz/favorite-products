package com.magalu.favoriteproducts.application.rest.request

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

data class FavoriteProductsRequest(
    @field:Size(min = 1, max = 25)
    @field:NotEmpty
    val productIds: List<Long>,
)
