package com.example.product_service.Entity;

import com.example.product_service.Enums.CategoryStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "category", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name", "parent_id"})
})

public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;      // nullable — null means top-level category

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryStatus status;   // ACTIVE, INACTIVE — this one's a genuinely fixed enum

    private Instant createdAt;
    private Instant updatedAt;
}
