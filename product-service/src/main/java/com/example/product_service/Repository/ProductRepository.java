package com.example.product_service.Repository;

import com.example.product_service.Entity.Product;
import com.example.product_service.Enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    Optional<Product> findByIdAndStatus(UUID id, ProductStatus status);


    @Query(value = """
        SELECT * FROM product p
        WHERE p.status = 'ACTIVE'
          AND (CAST(:categoryId AS uuid) IS NULL OR p.category_id = CAST(:categoryId AS uuid))
          AND (CAST(:attributeKey AS text) IS NULL
               OR p.attributes @> jsonb_build_object(CAST(:attributeKey AS text), CAST(:attributeValue AS text)))
          AND (
                CAST(:lastCreatedAt AS timestamptz) IS NULL
                OR (p.created_at, p.id) < (CAST(:lastCreatedAt AS timestamptz), CAST(:lastId AS uuid))
              )
        ORDER BY p.created_at DESC, p.id DESC
        LIMIT :limit
        """,
            nativeQuery = true)
    List<Product> search(@Param("categoryId") UUID categoryId,
                         @Param("attributeKey") String attributeKey,
                         @Param("attributeValue") String attributeValue,
                         @Param("lastCreatedAt") Instant lastCreatedAt,
                         @Param("lastId") UUID lastId,
                         @Param("limit") int limit);
}