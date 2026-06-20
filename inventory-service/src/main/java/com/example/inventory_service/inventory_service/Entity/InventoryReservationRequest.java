package com.example.inventory_service.inventory_service.Entity;

import com.example.inventory_service.inventory_service.Enum.ReservationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryReservationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inventoryReservationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id")
    private Inventory inventory;
    private Integer unitReserved;
    private LocalDateTime reservedAt;
    private LocalDateTime reservationExpiry;
    private Long orderId;
    private ReservationStatus reservationStatus;
    private boolean isReleased=false;

}
