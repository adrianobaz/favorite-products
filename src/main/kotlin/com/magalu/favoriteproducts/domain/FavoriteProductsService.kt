package com.magalu.favoriteproducts.domain

import com.magalu.favoriteproducts.domain.ClientsService.Companion.CLIENTS_RESOURCE_NAME
import com.magalu.favoriteproducts.domain.exception.ResourceNotFoundException
import com.magalu.favoriteproducts.domain.model.Client
import com.magalu.favoriteproducts.domain.model.Product
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.transaction.support.TransactionTemplate
import java.util.UUID

private val logger = KotlinLogging.logger {}

class FavoriteProductsService(
    private val clientsPort: ClientsPort,
    private val productsPort: ProductsPort,
    private val productsIntegrationPort: ProductsIntegrationPort,
    private val transactionTemplate: TransactionTemplate,
) {
    fun addFavoriteProducts(
        externalId: UUID,
        productsId: List<Long>,
    ): Client {
        val result =
            transactionTemplate.execute {
                val client =
                    clientsPort.searchWithFavoriteProducts(externalId)
                        ?: throw ResourceNotFoundException(CLIENTS_RESOURCE_NAME, externalId)
                val uniqueProductIds = productsId.toSet()
                val ids =
                    client.favoriteProducts
                        ?.takeUnless { it.isEmpty() }
                        ?.let { favoriteProducts ->
                            val favoriteProductIds = favoriteProducts.map { product -> product.id }
                            uniqueProductIds.filterNot { it in favoriteProductIds }
                        }?.toSet()

                if (ids != null) {
                    if (ids.isEmpty()) return@execute client
                    val newProducts = searchProductsAndApplyFiltering(ids)
                    return@execute newProducts.takeUnless { it.isEmpty() }?.let {
                        val products = newProducts + client.favoriteProducts
                        clientsPort
                            .update(
                                client.copy(favoriteProducts = products.toSet()),
                                true,
                            ).also {
                                logger.info { "Products=[$newProducts] added as new favorite to client=[$externalId]" }
                            }
                    } ?: client
                }

                val newProducts = searchProductsAndApplyFiltering(uniqueProductIds)
                return@execute newProducts.takeUnless { it.isEmpty() }?.let {
                    clientsPort
                        .update(
                            client.copy(favoriteProducts = it.toSet()),
                            true,
                        ).also {
                            logger.info { "Products=[$newProducts] added as favorite to client=[$externalId]" }
                        }
                } ?: client
            }
        return result!!
    }

    fun searchAllFavoriteProductsBy(externalId: UUID): Client =
        clientsPort.searchWithFavoriteProducts(externalId) ?: throw ResourceNotFoundException(CLIENTS_RESOURCE_NAME, externalId)

    private fun searchProductsAndApplyFiltering(ids: Set<Long>): List<Product> {
        val productsFromStorage = productsPort.searchProducts(ids.toList())
        val products =
            productsFromStorage.takeUnless { it.isEmpty() }?.let { productsFromStorage ->
                val productsFromStorageIds = productsFromStorage.map { it.id }
                val productsNotPresentOnStorage = ids.filterNot { it in productsFromStorageIds }
                val productsFromFakeStore =
                    productsNotPresentOnStorage.mapNotNull {
                        productsIntegrationPort.searchProduct(it)
                    }
                (productsFromStorage + productsFromFakeStore)
            } ?: ids.mapNotNull { productsIntegrationPort.searchProduct(it) }
        return products
    }
}
