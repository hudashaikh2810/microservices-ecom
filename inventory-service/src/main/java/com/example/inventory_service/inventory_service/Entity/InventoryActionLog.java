package com.example.inventory_service.inventory_service.Entity;
import com.example.inventory_service.inventory_service.Enum.InventoryActionStatus;
import com.example.inventory_service.inventory_service.Enum.InventoryActionType;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
        name = "inventory_action_log",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_inventory_action",
                        columnNames = {"order_id", "action_type"}
                )
        }
)
public class InventoryActionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false, length = 64)
    private String orderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false, length = 50)
    private InventoryActionType actionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private InventoryActionStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /* ---------- Lifecycle hooks ---------- */

    @PrePersist
    public void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = Instant.now();
    }

    /* ---------- Constructors ---------- */

    protected InventoryActionLog() {
        // JPA
    }

    public InventoryActionLog(
            String orderId,
            InventoryActionType actionType,
            InventoryActionStatus status) {
        this.orderId = orderId;
        this.actionType = actionType;
        this.status = status;
    }

    /* ---------- Getters & setters ---------- */

    public Long getId() {
        return id;
    }

    public String getOrderId() {
        return orderId;
    }

    public InventoryActionType getActionType() {
        return actionType;
    }

    public InventoryActionStatus getStatus() {
        return status;
    }

    public void setStatus(InventoryActionStatus status) {
        this.status = status;
    }
}

