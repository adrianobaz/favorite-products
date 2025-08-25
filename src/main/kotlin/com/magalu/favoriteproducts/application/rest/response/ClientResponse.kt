package com.magalu.favoriteproducts.application.rest.response

data class ClientResponse(
    val externalId: String,
    val name: String,
    val email: String,
    val favoriteProducts: List<ProductResponse>? = null,
)
