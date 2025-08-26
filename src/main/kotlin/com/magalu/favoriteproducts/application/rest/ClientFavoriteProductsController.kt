package com.magalu.favoriteproducts.application.rest

import com.magalu.favoriteproducts.application.rest.request.ClientRequest
import com.magalu.favoriteproducts.application.rest.request.ClientUpdateRequest
import com.magalu.favoriteproducts.application.rest.request.FavoriteProductsRequest
import com.magalu.favoriteproducts.application.rest.response.ClientResponse
import com.magalu.favoriteproducts.application.rest.response.ProductResponse
import com.magalu.favoriteproducts.domain.service.ClientsService
import com.magalu.favoriteproducts.domain.service.FavoriteProductsService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
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
    @Operation(
        summary = "Cadastro de cliente a partir do nome e e-mail",
        description = "Cadastra cliente a partir do nome e e-mail, retorna as informações do cadastro em caso de sucesso",
    )
    @PostMapping(produces = [MediaType.APPLICATION_JSON_VALUE], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun create(
        @Valid @RequestBody clientRequest: ClientRequest,
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

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Busca cliente a partir do ID externo gerado pela API",
        description = "Realiza a busca do cliente a partir do ID externo. Retorna as informações do mesmo em caso de sucesso",
    )
    @GetMapping("/{externalId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getByExternalId(
        @NotBlank
        @Pattern(
            regexp = UUIDV4_REGEX,
            message = "Invalid UUIDv4 format",
        )
        @PathVariable("externalId") externalId: String,
    ): ResponseEntity<ClientResponse> =
        ResponseEntity.ok(
            clientsService.searchByExternalId(UUID.fromString(externalId)).toResponse(),
        )

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Atualiza as informações de um cliente já cadastrado a partir do ID externo",
        description = "Realiza a atualização do nome e/ou email do cliente em questão",
    )
    @PatchMapping("/{externalId}", consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun update(
        @NotBlank
        @Pattern(
            regexp = UUIDV4_REGEX,
            message = "Invalid UUIDv4 format",
        )
        @PathVariable("externalId") clientExternalId: String,
        @Valid @RequestBody clientUpdateRequest: ClientUpdateRequest,
    ): ResponseEntity<ClientResponse> =
        ResponseEntity.ok(
            clientsService.update(UUID.fromString(clientExternalId), clientUpdateRequest.name, clientUpdateRequest.email).toResponse(),
        )

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Deleção de um cliente já previamente cadastrado a partir do ID externo",
        description = "Realiza a deleção do cliente a partir do ID externo. Retorna 204 em caso de sucesso",
    )
    @DeleteMapping("/{externalId}")
    fun deleteClientByExternalId(
        @NotBlank
        @Pattern(
            regexp = UUIDV4_REGEX,
            message = "Invalid UUIDv4 format",
        )
        @PathVariable("externalId") externalId: String,
    ): ResponseEntity<Any> {
        clientsService.deleteByExternalId(UUID.fromString(externalId))
        return ResponseEntity.noContent().build()
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Adiciona produto(s) a lista de favoritos do cliente",
        description =
            "Realiza a adição de produto(s) a lista de favoritos do cliente. " +
                "Necessario passar a lista de produtos e o ID externo do cliente",
    )
    @PostMapping(
        "/{externalId}/favorite-products",
        produces = [MediaType.APPLICATION_JSON_VALUE],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
    )
    fun addFavoriteProducts(
        @NotBlank
        @Pattern(
            regexp = UUIDV4_REGEX,
            message = "Invalid UUIDv4 format",
        )
        @PathVariable("externalId") clientExternalId: String,
        @Valid @RequestBody favoriteProductsRequest: FavoriteProductsRequest,
    ): ResponseEntity<List<ProductResponse>> {
        val client =
            favoriteProductsService.addFavoriteProducts(
                UUID.fromString(clientExternalId),
                favoriteProductsRequest.productIds,
            )
        return ResponseEntity.ok(client.favoriteProducts.toResponse())
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Busca o(s) produto(s) favorito(s) do cliente",
        description = "Realiza a busca do(s) produto(s) favorito(s) do cliente. Necessario pasar o ID externo do cliente",
    )
    @GetMapping("/{externalId}/favorite-products", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getFavoriteProducts(
        @NotBlank
        @Pattern(
            regexp = UUIDV4_REGEX,
            message = "Invalid UUIDv4 format",
        )
        @PathVariable("externalId") clientExternalId: String,
    ): ResponseEntity<List<ProductResponse>> {
        val client = favoriteProductsService.searchAllFavoriteProductsBy(UUID.fromString(clientExternalId))
        return ResponseEntity.ok(client.favoriteProducts.toResponse())
    }

    companion object {
        const val UUIDV4_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-4[0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$"
    }
}
