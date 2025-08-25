package com.magalu.favoriteproducts.infrastructure.jpa.schema

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
class RatingEmbedded(
    @Column(name = "rate")
    val rate: Double,
    @Column(name = "count")
    val count: Int,
)
