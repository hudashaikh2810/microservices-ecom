package com.order_service.order_servie.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_info")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;  // Reference to Order (not a JPA relation to avoid heavy joins)

    private String updateType;  // e.g., STATUS_UPDATED, PAYMENT_UPDATED

    @Column(length = 1000)
    private String updateDetails; // e.g., "Order moved from PACKED -> SHIPPED"

    private LocalDateTime updatedAt;

    @PrePersist
    public void setTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

    // getters & setters
}

