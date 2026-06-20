package com.example.inventory_service.inventory_service.Repository;

import com.example.inventory_service.inventory_service.Entity.Inventory;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory,Long> {

    Optional<Inventory> findBySku(String sku);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Inventory i WHERE i.sku = :sku")
    Optional<Inventory> findBySkuWithLock(@Param("sku") String sku);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Inventory i WHERE i.sku IN :skus ORDER BY i.sku")
    List<Inventory> findBySkusWithLock(@Param("skus") List<String> skus);



    @Modifying
    @Transactional
    @Query("UPDATE Inventory i SET i.reservedStock = i.reservedStock - :units, " +
            "i.lastUpdated = CURRENT_TIMESTAMP,i.totalStock=i.totalStock+:units  WHERE i.sku = :sku")
    void releaseReservedStock(@Param("sku") String sku, @Param("units") Integer units);

}
