package com.magalu.favoriteproducts.infrastructure.jpa.schema

import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.proxy.HibernateProxy
import java.math.BigDecimal
import java.time.OffsetDateTime

@Entity
@Table(name = "products")
class ProductEntity(
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    val id: Long,
    @Column(name = "title", nullable = false)
    val title: String,
    @Column(name = "image", nullable = false)
    val image: String,
    @Column(name = "price", nullable = false)
    val price: BigDecimal,
    @Embedded
    val rating: RatingEmbedded? = null,
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: OffsetDateTime? = null,
    @Column(name = "updated_at", nullable = false)
    val updatedAt: OffsetDateTime? = null,
) {
    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        val oEffectiveClass =
            if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
        val thisEffectiveClass =
            if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
        if (thisEffectiveClass != oEffectiveClass) return false
        other as ProductEntity

        return id == other.id
    }

    final override fun hashCode(): Int =
        if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass.hashCode() else javaClass.hashCode()
}
