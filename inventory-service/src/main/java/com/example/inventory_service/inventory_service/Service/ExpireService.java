package com.example.inventory_service.inventory_service.Service;

import com.example.inventory_service.inventory_service.Entity.InventoryReservationRequest;
import com.example.inventory_service.inventory_service.Repository.InventoryRepository;
import com.example.inventory_service.inventory_service.Repository.InventoryReservationRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpireService {
    @Autowired
    InventoryReservationRequestRepository reservationRepo;

    @Autowired
    InventoryRepository inventoryRepo;
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void expireAndFreeInBulk(Long orderId, LocalDateTime now) {

        int expiredCount =
                reservationRepo.expireAllExpiredForOrder(orderId, now);

        if (expiredCount > 0) {
            List<InventoryReservationRequest> inventoryReservationRequestList=reservationRepo.findExpiredReservationsForOrderId(orderId);
            for(InventoryReservationRequest request:inventoryReservationRequestList)
            {
                inventoryRepo.releaseReservedStock(request.getInventory().getSku(),request.getUnitReserved());
            }
            List<Long> inventoryReservationRequestId=inventoryReservationRequestList.stream().map(InventoryReservationRequest::getInventoryReservationId).toList();
            reservationRepo.markAsReleased(inventoryReservationRequestId);
        }
    }

}
