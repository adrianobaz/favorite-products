package com.magalu.favoriteproducts.infrastructure.fakestore

import com.magalu.favoriteproducts.infrastructure.fakestore.response.FakeStoreProductResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@FeignClient(name = "fakeStoreClient", url = "\${app.infrastructure.fake-store.url}")
interface FakeStoreClient {
    @GetMapping("/products", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getAllProducts(): List<FakeStoreProductResponse>

    @GetMapping("/products/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getProduct(
        @PathVariable("id") id: Long,
    ): FakeStoreProductResponse?
}
