package com.magalu.favoriteproducts.domain

import com.magalu.favoriteproducts.domain.model.Product

interface ProductsIntegrationPort {
    fun searchAllProducts(): List<Product>

    fun searchProduct(id: Long): Product?
}
