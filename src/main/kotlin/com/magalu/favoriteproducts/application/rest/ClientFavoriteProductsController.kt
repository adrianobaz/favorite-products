package com.magalu.favoriteproducts.application.rest

import com.magalu.favoriteproducts.application.rest.request.ClientRequest
import com.magalu.favoriteproducts.application.rest.request.ClientUpdateRequest
import com.magalu.favoriteproducts.application.rest.request.FavoriteProductsRequest
import com.magalu.favoriteproducts.application.rest.response.ClientResponse
import com.magalu.favoriteproducts.application.rest.response.ProductResponse
import com.magalu.favoriteproducts.domain.ClientsService
import com.magalu.favoriteproducts.domain.FavoriteProductsService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.util.UUID

@RestController
@RequestMapping("/v1/clients")
@Validated
class ClientFavoriteProductsController(
    private val clientsService: ClientsService,
    private val favoriteProductsService: FavoriteProductsService,
) {
    @PostMapping(produces = [MediaType.APPLICATION_JSON_VALUE], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun create(
        @RequestBody clientRequest: ClientRequest,
    ): ResponseEntity<ClientResponse> {
        val client = clientsService.create(clientRequest.toDomain())
        val location =
            ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{externalId}")
                .buildAndExpand(client.externalId)
                .toUri()
        return ResponseEntity.created(location).body(client.toResponse())
    }

    @GetMapping("/{externalId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getByExternalId(
        @PathVariable("externalId") externalId: String,
    ): ResponseEntity<ClientResponse> =
        ResponseEntity.ok(
            clientsService.searchByExternalId(UUID.fromString(externalId)).toResponse(),
        )

    @PatchMapping("/{externalId}", consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun update(
        @PathVariable("externalId") clientExternalId: String,
        @RequestBody clientUpdateRequest: ClientUpdateRequest,
    ): ResponseEntity<ClientResponse> =
        ResponseEntity.ok(
            clientsService.update(UUID.fromString(clientExternalId), clientUpdateRequest.name, clientUpdateRequest.email).toResponse(),
        )

    @DeleteMapping("/{externalId}")
    fun deleteClientByExternalId(
        @PathVariable("externalId") externalId: String,
    ): ResponseEntity<Any> {
        clientsService.deleteByExternalId(UUID.fromString(externalId))
        return ResponseEntity.noContent().build()
    }

    @PostMapping(
        "/{externalId}/favorite-products",
        produces = [MediaType.APPLICATION_JSON_VALUE],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
    )
    fun addFavoriteProducts(
        @PathVariable("externalId") clientExternalId: String,
        @RequestBody favoriteProductsRequest: FavoriteProductsRequest,
    ): ResponseEntity<List<ProductResponse>> {
        val client =
            favoriteProductsService.addFavoriteProducts(
                UUID.fromString(clientExternalId),
                favoriteProductsRequest.productIds,
            )
        return ResponseEntity.ok(client.favoriteProducts.toResponse())
    }

    @GetMapping("/{externalId}/favorite-products", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getFavoriteProducts(
        @PathVariable("externalId") clientExternalId: String,
    ): ResponseEntity<List<ProductResponse>> {
        val client = favoriteProductsService.searchAllFavoriteProductsBy(UUID.fromString(clientExternalId))
        return ResponseEntity.ok(client.favoriteProducts.toResponse())
    }
}
