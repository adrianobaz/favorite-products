package com.magalu.favoriteproducts.application.rest.response

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class ClientResponse(
    val externalId: String,
    val name: String,
    val email: String,
    val favoriteProducts: List<ProductResponse>? = null,
)
