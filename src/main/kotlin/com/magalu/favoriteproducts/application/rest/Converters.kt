package com.magalu.favoriteproducts.application.rest

import com.magalu.favoriteproducts.application.rest.request.ClientRequest
import com.magalu.favoriteproducts.application.rest.response.ClientResponse
import com.magalu.favoriteproducts.application.rest.response.ProductResponse
import com.magalu.favoriteproducts.application.rest.response.RatingResponse
import com.magalu.favoriteproducts.domain.model.Client
import com.magalu.favoriteproducts.domain.model.Product
import com.magalu.favoriteproducts.domain.model.Rating

fun ClientRequest.toDomain() =
    Client(
        name = this.name,
        email = this.email,
    )

fun Client.toResponse(withFavoriteProducts: Boolean = false) =
    ClientResponse(
        externalId = externalId.toString(),
        name = name,
        email = email,
        this.favoriteProducts?.takeIf { withFavoriteProducts }.toResponse(),
    )

fun Set<Product>?.toResponse() = this?.toList()?.map { it.toResponse() }

fun Product.toResponse() =
    ProductResponse(
        id,
        title,
        image,
        price,
        rating?.toResponse(),
    )

fun Rating.toResponse() = RatingResponse(rate, count)
