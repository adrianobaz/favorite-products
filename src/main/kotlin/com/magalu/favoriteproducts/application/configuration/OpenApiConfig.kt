package com.magalu.favoriteproducts.application.configuration

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {
    @Bean
    fun customOpenAPI(): OpenAPI {
        val securitySchemeName = "bearerAuth"

        return OpenAPI()
            .info(
                Info()
                    .title("API REST - Produtos Favoritos")
                    .version("v1")
                    .description(
                        "API responsável por prover operações de cadastro, atualização, busca e " +
                            "deleção de clientes. Assim como autenticação, autorização e cadastro de produtos favoritos " +
                            "para os clientes previamente existentes",
                    ),
            ).components(
                Components().addSecuritySchemes(
                    securitySchemeName,
                    SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"),
                ),
            ).addSecurityItem(SecurityRequirement().addList(securitySchemeName))
    }
}
