package com.magalu.favoriteproducts.infrastructure.jpa

import com.magalu.favoriteproducts.domain.ProductsPort
import com.magalu.favoriteproducts.domain.model.Product
import com.magalu.favoriteproducts.infrastructure.jpa.repositories.ProductsRepository
import org.springframework.stereotype.Component

@Component
class ProductStorageAdapter(
    private val productsRepository: ProductsRepository,
) : ProductsPort {
    override fun searchProducts(ids: List<Long>): List<Product> = productsRepository.findAllById(ids).map { it.toDomain() }
}
