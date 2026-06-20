package com.order_service.order_servie.Entity;

import com.order_service.order_servie.Enums.OrderStatus;
import com.order_service.order_servie.Enums.OrderType;
import com.order_service.order_servie.Enums.PaymentStatus;
import com.order_service.order_servie.Enums.PaymentType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @Column(nullable = false, unique = true)
    private String orderTrackingId;

    private Double amountToPay;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    // Only creation timestamp remains
    private LocalDateTime createdAt;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private OrderAddress deliveryAddress;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

