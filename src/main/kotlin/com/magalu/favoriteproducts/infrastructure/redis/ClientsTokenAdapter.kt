package com.magalu.favoriteproducts.infrastructure.redis

import com.magalu.favoriteproducts.domain.ClientsTokenPort
import com.magalu.favoriteproducts.domain.exception.UnauthorizedActionException
import com.magalu.favoriteproducts.domain.model.Client
import com.magalu.favoriteproducts.infrastructure.jpa.ClientStorageAdapter
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.UUID

private val logger = KotlinLogging.logger {}

@Component
class ClientsTokenAdapter(
    private val clientStorageAdapter: ClientStorageAdapter,
    private val redisTemplate: RedisTemplate<String, Any>,
) : ClientsTokenPort {
    override fun setEmail(email: String): Boolean {
        val key = "$EMAIL_PREFIX$email"
        runCatching {
            redisTemplate.opsForValue().set(key, email, Duration.ofMinutes(1))
        }.onFailure {
            logger.info { "Error to put on cache: ${it.message}" }
            return false
        }
        return true
    }

    override fun removeEmail(email: String): Boolean {
        val key = "$EMAIL_PREFIX$email"
        return runCatching {
            redisTemplate.delete(key)
        }.getOrElse {
            logger.info { "Error to delete on cache: ${it.message}" }
            return false
        }
    }

    override fun emailAlreadyExists(email: String): Boolean {
        val key = "$EMAIL_PREFIX$email"
        return runCatching {
            redisTemplate.opsForValue().get(key) != null
        }.getOrElse {
            logger.info { "Error to get on cache: ${it.message}" }
            return false
        }
    }

    override fun createMagicToken(
        externalId: UUID,
        ttlMinutes: Long,
    ): String {
        val token = UUID.randomUUID().toString()
        val key = "$MAGIC_TOKEN_PREFIX$token"
        runCatching {
            redisTemplate.opsForValue().set(key, externalId.toString(), Duration.ofMinutes(ttlMinutes))
        }.onFailure {
            logger.info { "Error to put on cache: ${it.message}" }
            throw UnauthorizedActionException("It's not possible obtain token now. Try later")
        }
        return token
    }

    override fun consumeMagicToken(token: String): Client? {
        val key = "$MAGIC_TOKEN_PREFIX$token"
        val ops = redisTemplate.opsForValue()
        val value = ops.get(key) ?: return null
        redisTemplate.delete(key)
        return clientStorageAdapter.search(UUID.fromString(value as String?))
    }

    companion object {
        private const val MAGIC_TOKEN_PREFIX = "magic-token::"
        private const val EMAIL_PREFIX = "email::"
    }
}
