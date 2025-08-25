package com.magalu.favoriteproducts

import io.hypersistence.utils.spring.repository.BaseJpaRepositoryImpl
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@EnableJpaRepositories(
    value = ["com.magalu.favoriteproducts.infrastructure.jpa.repositories"],
    repositoryBaseClass = BaseJpaRepositoryImpl::class,
)
@EnableFeignClients
@SpringBootApplication
class FavoriteProductsApplication

fun main(args: Array<String>) {
    runApplication<FavoriteProductsApplication>(*args)
}
