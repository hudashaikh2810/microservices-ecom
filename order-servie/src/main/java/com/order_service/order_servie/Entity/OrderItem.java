package com.order_service.order_servie.Entity;

import com.order_service.order_servie.Enums.OrderType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String skuId;

    private Integer quantity;

    private Double pricePerUnit;

    private Double totalItemPrice;

    @Enumerated(EnumType.STRING)
    private OrderType orderType;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    // Automatically compute totalItemPrice
    @PrePersist
    @PreUpdate
    public void calculateTotal() {
        if (pricePerUnit != null && quantity != null) {
            this.totalItemPrice = pricePerUnit * quantity;
        }
    }

    // getters & setters
}

