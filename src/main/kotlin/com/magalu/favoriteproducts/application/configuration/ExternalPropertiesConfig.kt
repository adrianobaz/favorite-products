package com.magalu.favoriteproducts.application.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.bind.ConstructorBinding
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
@ConfigurationPropertiesScan(basePackageClasses = [JwtProperties::class])
class ExternalPropertiesConfig

@ConfigurationProperties("app.jwt")
data class JwtProperties
    @ConstructorBinding
    constructor(
        val secret: String,
        val issuer: String,
        val expirationMinutes: Long,
    )
