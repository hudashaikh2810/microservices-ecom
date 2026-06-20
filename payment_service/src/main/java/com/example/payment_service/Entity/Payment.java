package com.example.payment_service.Entity;

import com.example.payment_service.Enums.PaymentStatus;
import com.example.payment_service.Enums.PaymentType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "payments",
        indexes = {
                @Index(name = "idx_order_id", columnList = "orderId"),
                @Index(name = "idx_gateway_payment_id", columnList = "gatewayPaymentId")
        }
)
@Getter
@Setter
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    // Order Service reference
    @Column(nullable = false)
    private Long orderId;

    // Gateway references
    @Column(unique = true)
    private String gatewayPaymentId;

    @Column(unique = true)
    private String gatewayOrderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentType paymentType;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    // Failure details (if any)
    private String failureReason;

    // Retry support
    @Column(nullable = false)
    private int attemptNumber;

    // Webhook idempotency
    @Column(unique = true)
    private String webhookEventId;

    // Auditing
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.paymentStatus = PaymentStatus.INITIATED;
        this.attemptNumber = 1;
        this.currency = "INR";
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // getters and setters
}

