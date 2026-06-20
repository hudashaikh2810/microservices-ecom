package com.example.product_service.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int imageId;
    @Column(nullable = false)
    private String url;
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "sku_id")
    Sku sku;
    private boolean isPrimary;
}