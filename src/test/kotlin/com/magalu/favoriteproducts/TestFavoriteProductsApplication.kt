package com.magalu.favoriteproducts

import org.springframework.boot.fromApplication
import org.springframework.boot.with

fun main(args: Array<String>) {
    fromApplication<FavoriteProductsApplication>().with(TestcontainersConfiguration::class).run(*args)
}
