package com.magalu.favoriteproducts.infrastructure.jpa

import com.magalu.favoriteproducts.domain.model.Client
import com.magalu.favoriteproducts.domain.model.Product
import com.magalu.favoriteproducts.domain.model.Rating
import com.magalu.favoriteproducts.infrastructure.jpa.schema.ClientEntity
import com.magalu.favoriteproducts.infrastructure.jpa.schema.ProductEntity
import com.magalu.favoriteproducts.infrastructure.jpa.schema.RatingEmbedded

fun Client.toEntity(withFavoriteProducts: Boolean = false) =
    ClientEntity(
        id = this.id,
        externalId = this.externalId,
        name = this.name,
        email = this.email,
        favoriteProducts =
            favoriteProducts?.takeIf { withFavoriteProducts && it.isNotEmpty() }?.let {
                it.map { it.toEntity() }.toSet()
            },
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
    )

fun ClientEntity.toDomain(withFavoriteProducts: Boolean = false) =
    Client(
        id = this.id,
        externalId = this.externalId,
        name = this.name,
        email = this.email,
        favoriteProducts =
            this.favoriteProducts
                ?.takeIf {
                    withFavoriteProducts && it.isNotEmpty()
                }?.let { it.map { it.toDomain() }.toSet() },
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
    )

fun Product.toEntity() =
    ProductEntity(
        id,
        title,
        image,
        price,
        rating?.toEntity(),
        createdAt,
        updatedAt,
    )

fun Rating.toEntity() = RatingEmbedded(rate, count)

fun ProductEntity.toDomain() =
    Product(
        id,
        title,
        image,
        price,
        rating?.toDomain(),
        createdAt,
        updatedAt,
    )

fun RatingEmbedded.toDomain() = Rating(rate, count)
