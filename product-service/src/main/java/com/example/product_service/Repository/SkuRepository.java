package com.example.product_service.Repository;

import com.example.product_service.Entity.Sku;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SkuRepository extends JpaRepository<Sku,String> {
    @Query("select s from Sku s where s.product.productId=:productId and s.isDefaultSku=true")
    Optional<Sku> findDefaultSkuByProductId(Long productId);

}
