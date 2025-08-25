package com.magalu.favoriteproducts.domain

import com.magalu.favoriteproducts.domain.model.Product

interface ProductsPort {
    fun searchProducts(ids: List<Long>): List<Product>
}
