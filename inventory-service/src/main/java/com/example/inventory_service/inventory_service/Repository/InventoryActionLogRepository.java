package com.example.inventory_service.inventory_service.Repository;

import com.example.inventory_service.inventory_service.Entity.InventoryActionLog;
import com.example.inventory_service.inventory_service.Enum.InventoryActionType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryActionLogRepository
        extends JpaRepository<InventoryActionLog, Long> {

    boolean existsByOrderIdAndActionType(
            String orderId,
            InventoryActionType actionType
    );
}
