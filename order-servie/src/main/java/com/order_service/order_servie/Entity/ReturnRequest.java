package com.order_service.order_servie.Entity;

import com.order_service.order_servie.Enums.ReturnItemStatus;
import com.order_service.order_servie.Enums.ReturnStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "return_requests")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReturnRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String returnTrackingId;

    @ManyToOne
    @JoinColumn(name = "order_item_id")
    private OrderItem orderItem;

    private int quantityReturned;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order originalOrder;

    private String reason;

    @Enumerated(EnumType.STRING)
    private ReturnStatus returnStatus;

    // REQUESTED, PICKUP_SCHEDULED, PICKED, QC_APPROVED, REFUND_INITIATED, REFUND_COMPLETED

    @Enumerated(EnumType.STRING)
    private ReturnItemStatus returnItemStatus;

    private Double refundAmount;

    private LocalDateTime requestedAt;
    private LocalDateTime pickedAt;
    // <-- when item actually picked
}
