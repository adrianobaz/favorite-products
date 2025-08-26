package com.magalu.favoriteproducts.application.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import io.lettuce.core.ClientOptions
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.time.Duration

private val logger = KotlinLogging.logger {}

@Configuration
@EnableCaching
class RedisCacheConfig(
    private val cachingAsideProperties: CachingAsideProperties,
    private val objectMapper: ObjectMapper,
    @param:Value("\${spring.redis.host}") private val host: String,
    @param:Value("\${spring.redis.port}") private val port: Int,
) {
    @Bean
    fun lettuceConnectionFactory(): RedisConnectionFactory {
        val clientConfig =
            LettuceClientConfiguration
                .builder()
                .commandTimeout(Duration.ofSeconds(5))
                .shutdownTimeout(Duration.ZERO)
                .clientOptions(
                    ClientOptions
                        .builder()
                        .autoReconnect(true)
                        .build(),
                ).build()

        val serverConfig = RedisStandaloneConfiguration(host, port)

        return LettuceConnectionFactory(serverConfig, clientConfig)
    }

    @Bean
    fun redisTemplate(rcf: RedisConnectionFactory): RedisTemplate<String, Any> {
        val template = RedisTemplate<String, Any>()
        template.connectionFactory = rcf

        val serializer = GenericJackson2JsonRedisSerializer(objectMapper)

        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = serializer
        template.hashKeySerializer = StringRedisSerializer()
        template.hashValueSerializer = serializer
        template.afterPropertiesSet()
        return template
    }

    @Bean
    fun cacheManager(redisConnectionFactory: RedisConnectionFactory): RedisCacheManager {
        val valueSer = GenericJackson2JsonRedisSerializer(objectMapper)
        val defaultCacheConfig =
            RedisCacheConfiguration
                .defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .serializeValuesWith(
                    RedisSerializationContext.SerializationPair.fromSerializer(valueSer),
                )

        val cacheConfigurations =
            cachingAsideProperties.keys.mapValues { (_, value) ->
                defaultCacheConfig.entryTtl(Duration.ofMinutes(value))
            }

        return RedisCacheManager
            .builder(redisConnectionFactory)
            .cacheDefaults(defaultCacheConfig)
            .withInitialCacheConfigurations(cacheConfigurations)
            .build()
            .also { logger.info { "Cached: ${cachingAsideProperties.keys}" } }
    }
}
