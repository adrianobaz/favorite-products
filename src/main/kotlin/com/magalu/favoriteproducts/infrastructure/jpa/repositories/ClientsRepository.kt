package com.magalu.favoriteproducts.infrastructure.jpa.repositories

import com.magalu.favoriteproducts.infrastructure.jpa.schema.ClientEntity
import io.hypersistence.utils.spring.repository.BaseJpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.util.UUID

@Repository
interface ClientsRepository : BaseJpaRepository<ClientEntity, Long> {
    @Query("SELECT c FROM ClientEntity c LEFT JOIN FETCH c.favoriteProducts WHERE c.externalId = :externalId")
    fun searchAllFavoriteProducts(
        @Param("externalId") externalId: UUID,
    ): ClientEntity

    @Query(
        """
        SELECT
            c.id,
            c.external_id,
            c.name,
            c.email,
            c.created_at,
            c.updated_at
        FROM clients c
        WHERE c.external_id = :externalId
        """,
        nativeQuery = true,
    )
    fun findByExternalId(
        @Param("externalId") externalId: UUID,
    ): ClientEntity

    @Modifying
    @Transactional
    @Query(
        """
        UPDATE 
            clients 
        SET 
            name = :name, email = :email, updated_at = :updatedAt 
        WHERE external_id = :externalId
    """,
        nativeQuery = true,
    )
    fun updateByOnly(
        @Param("externalId") externalId: UUID,
        @Param("name") name: String,
        @Param("email") email: String,
        @Param("updatedAt") updatedAt: OffsetDateTime,
    ): Int

    @Query("SELECT EXISTS (SELECT 1 FROM clients c WHERE c.email = :email)", nativeQuery = true)
    fun existsByEmail(
        @Param("email") email: String,
    ): Boolean
}
