package com.example.product_service.Repository;

import com.example.product_service.DTO.ProductMetaDto;
import com.example.product_service.Entity.Product;
import com.example.product_service.Enums.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> , JpaSpecificationExecutor<Product> {

    Page<Product> findByCategoryName(Category category,Pageable pageable);
    @Query("""
SELECT new com.example.product_service.DTO.ProductMetaDto(
    p.productId,
    p.productName,
    p.productDescription,
    p.category,
    s.color,
    s.size,
    s.price,
    img.url,
    s.isAvailable
)
FROM Product p
JOIN p.skus s
LEFT JOIN s.images img ON img.isPrimary = true
WHERE p.category = :category
AND s.isDefaultSku = true And p.productId=:id
""")
    Optional<ProductMetaDto> findByCategoryNameWithDefaultSku(Category category, Long id);
    @Query("""
SELECT new com.example.product_service.DTO.ProductMetaDto(
   p.productId,
    p.productName,
    p.productDescription,
    p.category,
    s.color,
    s.size,
    s.price,
    img.url,
    s.isAvailable
)
FROM Product p
JOIN p.skus s
LEFT JOIN s.images img ON img.isPrimary = true
WHERE p.category = :category
AND s.isDefaultSku = true
""")
    Page<ProductMetaDto> findByCategoryNameWithDefaultSkuPage(Category category,Pageable p);

}
