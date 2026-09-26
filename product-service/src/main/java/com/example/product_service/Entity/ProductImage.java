package com.example.product_service.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "product_image")
@Getter
@Setter
@NoArgsConstructor
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private String url;

    private String altText;

    @Column(nullable = false)
    private Integer displayOrder;   // 0 = primary/cover image

    private String colorVariant;    // nullable — e.g. "black", if image is color-specific

    @Column(nullable = false)
    private boolean isPrimary;      // convenience flag instead of always checking displayOrder == 0

    private Instant createdAt;
}