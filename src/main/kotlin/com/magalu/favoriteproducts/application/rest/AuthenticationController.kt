package com.magalu.favoriteproducts.application.rest

import com.magalu.favoriteproducts.application.configuration.JwtProperties
import com.magalu.favoriteproducts.application.rest.ClientFavoriteProductsController.Companion.UUIDV4_REGEX
import com.magalu.favoriteproducts.application.security.JwtHandler
import com.magalu.favoriteproducts.application.security.JwtResponse
import com.magalu.favoriteproducts.application.security.TokenRequest
import com.magalu.favoriteproducts.domain.service.ClientsService
import io.github.oshai.kotlinlogging.KotlinLogging
import io.swagger.v3.oas.annotations.Operation
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import java.time.Duration
import java.util.UUID

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("/auth")
@Validated
class AuthenticationController(
    private val jwtHandler: JwtHandler,
    private val clientsService: ClientsService,
    private val jwtProps: JwtProperties,
    @param:Value("\${app.magic-link.ttl-minutes}") private val magicTtl: Long,
) {
    @Operation(
        summary = "Gerar um token para autenticação do cliente",
        description = "Realiza a geração do token JWT do cliente. Necessario enviar o ID externo do mesmo",
    )
    @PostMapping("/token/request")
    fun request(
        request: HttpServletRequest,
        @Valid @RequestBody req: TokenRequest,
    ): ResponseEntity<Any> {
        val client = clientsService.searchByExternalId(UUID.fromString(req.externalId))

        val token = clientsService.createMagicToken(client.externalId, magicTtl)
        val scheme = request.scheme
        val serverName = request.serverName
        val serverPort = 8080
        val contextPath = request.contextPath

        val link = "$scheme://$serverName:$serverPort$contextPath$ROOT_PATH$PATH_CONSUME_TOKEN?token=$token"
        logger.info { "Magic link for ${client.email}: $link (expires in $magicTtl minutes)" }
        val headers = HttpHeaders()
        headers.location = URI.create(link)
        return ResponseEntity.status(HttpStatus.SEE_OTHER).headers(headers).build()
    }

    @Operation(
        summary = "Obtem o token JWT contendo as informações do cliente para fins de autenticação/autorização",
        description = "Realiza a obtenção do token JWT do cliente. Necessario enviar o token gerado na geração do mesmo via link mágico",
    )
    @GetMapping(PATH_CONSUME_TOKEN)
    fun consume(
        @NotBlank
        @Pattern(
            regexp = UUIDV4_REGEX,
            message = "Invalid UUIDv4 format",
        )
        @RequestParam("token") token: String,
    ): ResponseEntity<JwtResponse> {
        val client = clientsService.consumeMagicToken(token)
        val jwt = jwtHandler.generateToken(client)
        val expiresIn = Duration.ofMinutes(jwtProps.expirationMinutes).seconds
        return ResponseEntity.ok(JwtResponse(jwt, expiresIn))
    }

    companion object {
        private const val ROOT_PATH = "/auth"
        private const val PATH_CONSUME_TOKEN = "/token/consume"
    }
}
