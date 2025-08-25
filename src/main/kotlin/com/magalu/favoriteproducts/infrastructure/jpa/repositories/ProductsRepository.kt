package com.magalu.favoriteproducts.infrastructure.jpa.repositories

import com.magalu.favoriteproducts.infrastructure.jpa.schema.ProductEntity
import io.hypersistence.utils.spring.repository.BaseJpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductsRepository : BaseJpaRepository<ProductEntity, Long>
