package com.magalu.favoriteproducts.infrastructure.fakestore

import com.magalu.favoriteproducts.domain.ProductsIntegrationPort
import com.magalu.favoriteproducts.infrastructure.fakestore.response.toDomain
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component

@Component
class FakeStoreAdapter(
    private val fakeStoreClient: FakeStoreClient,
) : ProductsIntegrationPort {
    override fun searchAllProducts() = fakeStoreClient.getAllProducts().map { it.toDomain() }

    @Cacheable(PRODUCTS_CACHE_KEY, key = "#id")
    override fun searchProduct(id: Long) = fakeStoreClient.getProduct(id)?.toDomain()

    companion object {
        const val PRODUCTS_CACHE_KEY = "products"
    }
}
