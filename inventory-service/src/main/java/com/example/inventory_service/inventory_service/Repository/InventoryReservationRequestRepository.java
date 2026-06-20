package com.example.inventory_service.inventory_service.Repository;

import com.example.inventory_service.inventory_service.Entity.InventoryReservationRequest;
import com.example.inventory_service.inventory_service.Enum.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InventoryReservationRequestRepository extends JpaRepository<InventoryReservationRequest,Long> {
    @Query("Select req from InventoryReservationRequest req where req.orderId=:orderId ")
    List<InventoryReservationRequest> findByOrderId(Long orderId);


    @Modifying
    @Query("""
UPDATE InventoryReservationRequest r
SET r.reservationStatus = com.example.inventory_service.inventory_service.Enum.ReservationStatus.EXPIRED
WHERE r.orderId = :orderId
  AND r.reservationStatus = com.example.inventory_service.inventory_service.Enum.ReservationStatus.RESERVED
  AND r.reservationExpiry < :now
""")
    int expireAllExpiredForOrder(@Param("orderId") Long orderId,
                                 @Param("now") LocalDateTime now);

    @Modifying
    @Query("""
UPDATE InventoryReservationRequest r
SET r.reservationStatus = com.example.inventory_service.inventory_service.Enum.ReservationStatus.CONFIRMED
, r.isReleased=true
WHERE r.orderId = :orderId
  AND r.reservationStatus = com.example.inventory_service.inventory_service.Enum.ReservationStatus.RESERVED
  AND r.reservationExpiry >= :now And r.isReleased=false
""")
    int confirmActiveReservationsForOrder(@Param("orderId") Long orderId,
                                          @Param("now") LocalDateTime now);

    @Modifying
    @Transactional
    @Query("UPDATE InventoryReservationRequest r SET r.reservationStatus = com.example.inventory_service.inventory_service.Enum.ReservationStatus.EXPIRED " +
            "WHERE r.reservationStatus = com.example.inventory_service.inventory_service.Enum.ReservationStatus.RESERVED AND r.reservationExpiry < :now")
    int markExpiredReservations(@Param("now") LocalDateTime now);

    @Query("SELECT r FROM InventoryReservationRequest r JOIN FETCH r.inventory " +
            "WHERE r.reservationStatus = com.example.inventory_service.inventory_service.Enum.ReservationStatus.EXPIRED AND r.isReleased = false")
    List<InventoryReservationRequest> findExpiredUnreleasedReservations();

    @Query("SELECT r FROM InventoryReservationRequest r JOIN FETCH r.inventory " +
            "WHERE r.reservationStatus = com.example.inventory_service.inventory_service.Enum.ReservationStatus.EXPIRED AND r.isReleased = false and r.orderId=:orderId")

    List<InventoryReservationRequest> findExpiredReservationsForOrderId(Long orderId);

    @Modifying
    @Transactional
    @Query("UPDATE InventoryReservationRequest r SET r.isReleased = true " +
            "WHERE r.isReleased=false and r.inventoryReservationId IN :ids")
    void markAsReleased(@Param("ids") List<Long> ids);

    @Modifying
    @Transactional
    @Query("UPDATE InventoryReservationRequest r " +
            "SET r.reservationStatus = :status, r.isReleased = true " +
            "WHERE r.inventoryReservationId IN :ids " +
            "AND r.isReleased = false")  // This is the key - prevents double processing
    int updateStatusByIds(@Param("ids") List<Long> ids,
                          @Param("status") ReservationStatus status);


}
