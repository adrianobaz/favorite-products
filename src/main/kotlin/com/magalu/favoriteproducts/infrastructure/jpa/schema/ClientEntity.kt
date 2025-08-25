package com.magalu.favoriteproducts.infrastructure.jpa.schema

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.proxy.HibernateProxy
import org.hibernate.type.SqlTypes
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "clients")
class ClientEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "clients_seq")
    @SequenceGenerator(name = "clients_seq", sequenceName = "clients_id_seq", allocationSize = 1)
    @JdbcTypeCode(SqlTypes.BIGINT)
    @Column(name = "id", nullable = false, updatable = false)
    var id: Long? = null,
    @Column(name = "external_id", nullable = false, unique = true, updatable = false)
    val externalId: UUID,
    @Column(name = "name", nullable = false)
    val name: String,
    @Column(name = "email", nullable = false, unique = true)
    val email: String,
    @ManyToMany(cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinTable(
        name = "client_product",
        joinColumns = [JoinColumn(name = "client_id")],
        inverseJoinColumns = [JoinColumn(name = "product_id")],
    )
    val favoriteProducts: Set<ProductEntity>? = setOf(),
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: OffsetDateTime? = null,
    @Column(name = "updated_at", nullable = false)
    val updatedAt: OffsetDateTime? = null,
) {
    fun addProducts(productsEntity: Set<ProductEntity>): Set<ProductEntity>? = favoriteProducts?.plus(productsEntity)

    fun removeProducts(productsEntity: Set<ProductEntity>): Set<ProductEntity>? = favoriteProducts?.minus(productsEntity)

    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        val oEffectiveClass =
            if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
        val thisEffectiveClass =
            if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
        if (thisEffectiveClass != oEffectiveClass) return false
        other as ClientEntity

        return id != null && (id == other.id || externalId == other.externalId)
    }

    final override fun hashCode(): Int =
        if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass.hashCode() else javaClass.hashCode()
}
