package com.magalu.favoriteproducts.domain.service

import com.magalu.favoriteproducts.domain.ClientsPort
import com.magalu.favoriteproducts.domain.ClientsTokenPort
import com.magalu.favoriteproducts.domain.exception.BusinessRuleViolationException
import com.magalu.favoriteproducts.domain.exception.ErrorCode
import com.magalu.favoriteproducts.domain.exception.ResourceNotFoundException
import com.magalu.favoriteproducts.domain.exception.UnauthorizedActionException
import com.magalu.favoriteproducts.domain.model.Client
import io.github.oshai.kotlinlogging.KotlinLogging
import java.time.OffsetDateTime
import java.util.UUID

private val logger = KotlinLogging.logger {}

class ClientsService(
    private val clientsPort: ClientsPort,
    private val clientsTokenPort: ClientsTokenPort,
) {
    fun create(client: Client): Client =
        runCatching {
            val now = OffsetDateTime.now()
            if (clientsTokenPort.emailAlreadyExists(client.email)) {
                throw BusinessRuleViolationException(
                    ErrorCode.EMAIL_ALREADY_REGISTERED.message,
                    ErrorCode.EMAIL_ALREADY_REGISTERED.name,
                )
            }

            if (clientsPort.existsByEmail(client.email)) {
                clientsTokenPort.setEmail(client.email)
                BusinessRuleViolationException(
                    ErrorCode.EMAIL_ALREADY_REGISTERED.message,
                    ErrorCode.EMAIL_ALREADY_REGISTERED.name,
                )
            }
            clientsPort.create(client.copy(createdAt = now, updatedAt = now))
        }.onSuccess {
            clientsTokenPort.setEmail(it.email)
            logger.info { "Successfully create client=[${it.externalId}]" }
        }.getOrElse {
            logger.info { "Error on create client=[${client.email}] | Error=[${it.message}]" }
            throw it
        }

    fun update(
        externalId: UUID,
        name: String?,
        email: String?,
    ): Client {
        val result =
            runCatching {
                val now = OffsetDateTime.now()
                val client = searchByExternalId(externalId)
                val nameToUpdate = name ?: client.name
                if (email != null && clientsTokenPort.emailAlreadyExists(email)) {
                    throw BusinessRuleViolationException(
                        ErrorCode.EMAIL_ALREADY_REGISTERED.message,
                        ErrorCode.EMAIL_ALREADY_REGISTERED.name,
                    )
                }
                val emailToUpdate = email ?: client.email
                val newClient = clientsPort.updateByOnly(client.externalId, nameToUpdate, emailToUpdate, now)
                Pair(newClient, client)
            }.onSuccess {
                val result =
                    if (it.first.email != it.second.email) {
                        val isRemoved = clientsTokenPort.removeEmail(it.second.email)
                        val isSet = clientsTokenPort.setEmail(it.first.email)
                        isRemoved && isSet
                    } else {
                        true
                    }
                logger.info { "Successfully update client=[$externalId] | From cache: $result" }
            }.getOrElse {
                logger.info { "Error on update client=[$externalId] | Error=[${it.message}]" }
                throw it
            }
        return result.first
    }

    fun searchByExternalId(externalId: UUID): Client =
        clientsPort.search(externalId)?.let {
            it.also { clientsTokenPort.setEmail(it.email) }
        } ?: throw ResourceNotFoundException(CLIENTS_RESOURCE_NAME, externalId)

    fun deleteByExternalId(externalId: UUID) {
        runCatching {
            clientsPort.delete(externalId)
        }.onSuccess {
            val result = clientsTokenPort.removeEmail(it)
            logger.info { "Successfully delete client=[$externalId] | From cache=[$result]" }
        }.onFailure {
            throw it
        }
    }

    fun createMagicToken(
        externalId: UUID,
        ttlMinutes: Long,
    ): String = clientsTokenPort.createMagicToken(externalId, ttlMinutes)

    fun consumeMagicToken(token: String): Client =
        clientsTokenPort.consumeMagicToken(token) ?: throw UnauthorizedActionException("Invalid or expired token")

    companion object {
        const val CLIENTS_RESOURCE_NAME = "clients"
    }
}
