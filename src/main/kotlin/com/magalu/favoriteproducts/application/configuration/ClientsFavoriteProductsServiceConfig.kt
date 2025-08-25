package com.magalu.favoriteproducts.application.configuration

import com.magalu.favoriteproducts.domain.ClientsPort
import com.magalu.favoriteproducts.domain.ClientsService
import com.magalu.favoriteproducts.domain.ClientsTokenPort
import com.magalu.favoriteproducts.domain.FavoriteProductsService
import com.magalu.favoriteproducts.domain.ProductsIntegrationPort
import com.magalu.favoriteproducts.domain.ProductsPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.support.TransactionTemplate

@Configuration
class ClientsFavoriteProductsServiceConfig(
    private val clientsPort: ClientsPort,
) {
    @Bean
    fun favoriteProductDomainService(
        productsPort: ProductsPort,
        productsIntegrationPort: ProductsIntegrationPort,
        transactionTemplate: TransactionTemplate,
    ) = FavoriteProductsService(clientsPort, productsPort, productsIntegrationPort, transactionTemplate)

    @Bean
    fun clientsDomainService(clientsTokenPort: ClientsTokenPort) = ClientsService(clientsPort, clientsTokenPort)
}
