package com.magalu.favoriteproducts.application.configuration

import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.time.Duration

@Configuration
@EnableCaching
class RedisConfig {
    @Bean
    fun redisTemplate(rcf: RedisConnectionFactory): RedisTemplate<String, Any> {
        val template = RedisTemplate<String, Any>()
        template.connectionFactory = rcf

        val serializer = Jackson2JsonRedisSerializer(Any::class.java)

        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = serializer
        template.hashKeySerializer = StringRedisSerializer()
        template.hashValueSerializer = serializer
        template.afterPropertiesSet()
        return template
    }

    @Bean
    fun cacheManager(redisConnectionFactory: RedisConnectionFactory): RedisCacheManager {
        val defaultCacheConfig =
            RedisCacheConfiguration
                .defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .serializeValuesWith(
                    RedisSerializationContext.SerializationPair.fromSerializer(GenericJackson2JsonRedisSerializer()),
                )

        val cacheConfigurations =
            mapOf(
                "products" to
                    RedisCacheConfiguration
                        .defaultCacheConfig()
                        .serializeValuesWith(
                            RedisSerializationContext.SerializationPair.fromSerializer(GenericJackson2JsonRedisSerializer()),
                        ).entryTtl(Duration.ofMinutes(5)),
            )

        return RedisCacheManager
            .builder(redisConnectionFactory)
            .cacheDefaults(defaultCacheConfig)
            .withInitialCacheConfigurations(cacheConfigurations)
            .build()
    }
}
