package com.example.inventory_service.inventory_service.Entity;

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
public class Inventory {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private String sku;
    private Integer totalStock;
    private Integer reservedStock;
    private LocalDateTime lastUpdated;

    @OneToMany(mappedBy="inventory",cascade= CascadeType.ALL)
    private List<InventoryReservationRequest> inventoryReservationRequest;

}
