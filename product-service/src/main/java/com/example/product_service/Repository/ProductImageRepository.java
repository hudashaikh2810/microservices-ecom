package com.example.product_service.Repository;

import com.example.product_service.Entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductImageRepository extends JpaRepository<ProductImage, UUID> {

    @Modifying
    @Query("UPDATE ProductImage i SET i.isPrimary = false WHERE i.product.id = :productId AND i.isPrimary = true")
    void unsetPrimaryForProduct(@Param("productId") UUID productId);

    List<ProductImage> findByProductIdOrderByDisplayOrder(UUID productId);

    @Query("SELECT i FROM ProductImage i WHERE i.product.id IN :productIds AND i.isPrimary = true")
    List<ProductImage> findPrimaryImagesFor(@Param("productIds") List<UUID> productIds);

    Optional<ProductImage> findFirstByProductIdOrderByDisplayOrder(UUID productId);

    Optional<ProductImage> findFirstByProductIdAndIdNotOrderByDisplayOrder(UUID productId, UUID excludeImageId);
}
