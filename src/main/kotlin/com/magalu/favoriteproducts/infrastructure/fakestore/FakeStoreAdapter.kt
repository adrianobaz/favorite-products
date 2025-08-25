package com.magalu.favoriteproducts.infrastructure.fakestore

import com.magalu.favoriteproducts.domain.ProductsIntegrationPort
import com.magalu.favoriteproducts.domain.model.Product
import com.magalu.favoriteproducts.infrastructure.fakestore.response.toDomain
import org.springframework.stereotype.Component

@Component
class FakeStoreAdapter(
    private val fakeStoreClient: FakeStoreClient,
) : ProductsIntegrationPort {
    override fun searchAllProducts() = fakeStoreClient.getAllProducts().map { it.toDomain() }

    override fun searchProduct(id: Long): Product? = fakeStoreClient.getProduct(id)?.toDomain()
}
