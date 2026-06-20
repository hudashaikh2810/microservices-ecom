package com.order_service.order_servie.Entity;

import com.order_service.order_servie.Enums.ExchangeStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "exchange_requests")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String exchangeTrackingId;

    @ManyToOne
    @JoinColumn(name = "old_order_item_id")
    private OrderItem oldOrderItem;

    @ManyToOne
    @JoinColumn(name = "original_order_id")
    private Order originalOrder;

    @OneToOne
    @JoinColumn(name = "replacement_order_id")
    private Order replacementOrder;   // NEW full order

    @OneToOne
    @JoinColumn(name = "new_order_item_id")
    private OrderItem newOrderItem;   // Item inside replacementOrder

    private Double priceDifference;

    @Enumerated(EnumType.STRING)
    private ExchangeStatus exchangeStatus;

    private LocalDateTime requestedAt;
    private LocalDateTime processedAt;
}

