package com.magalu.favoriteproducts.infrastructure.jpa

import com.magalu.favoriteproducts.domain.ClientsPort
import com.magalu.favoriteproducts.domain.exception.BusinessRuleViolationException
import com.magalu.favoriteproducts.domain.exception.ErrorCode
import com.magalu.favoriteproducts.domain.model.Client
import com.magalu.favoriteproducts.infrastructure.jpa.repositories.ClientsRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.util.UUID

@Component
class ClientStorageAdapter(
    private val clientsRepository: ClientsRepository,
) : ClientsPort {
    override fun create(client: Client): Client = clientsRepository.persist(client.toEntity()).toDomain()

    override fun update(
        client: Client,
        isUpdatedWithFavoriteProducts: Boolean,
    ): Client = clientsRepository.merge(client.toEntity(isUpdatedWithFavoriteProducts)).toDomain(isUpdatedWithFavoriteProducts)

    @Transactional
    override fun delete(externalId: UUID): String {
        val clientEntity = clientsRepository.findByExternalId(externalId)
        clientsRepository.delete(clientEntity)
        return clientEntity.email
    }

    override fun search(externalId: UUID): Client = clientsRepository.findByExternalId(externalId).toDomain()

    override fun searchWithFavoriteProducts(externalId: UUID): Client =
        clientsRepository.searchAllFavoriteProducts(externalId).toDomain(true)

    override fun updateByOnly(
        externalId: UUID,
        name: String,
        email: String,
        updatedAt: OffsetDateTime,
    ): Client {
        val result = clientsRepository.updateByOnly(externalId, name, email, updatedAt)
        return when (result) {
            1 -> search(externalId)
            else -> throw BusinessRuleViolationException(ErrorCode.UNEXPECTED_ERROR.message, ErrorCode.UNEXPECTED_ERROR.name)
        }
    }

    override fun existsByEmail(email: String): Boolean = clientsRepository.existsByEmail(email)
}
