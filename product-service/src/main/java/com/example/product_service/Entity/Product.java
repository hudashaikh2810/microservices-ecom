package com.example.product_service.Entity;

import com.example.product_service.Enums.Category;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column(nullable = false, unique = true)
    private String productName;

    @Column(nullable = false, length = 1000)
    private String productDescription;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    /**
     * Product owns SKUs
     */
    @OneToMany(
            mappedBy = "product",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Sku> skus = new ArrayList<>();

    /**
     * Convenience methods (VERY IMPORTANT)
     */
    public void addSku(Sku sku) {
        skus.add(sku);
        sku.setProduct(this);
    }

    public void removeSku(Sku sku) {
        skus.remove(sku);
        sku.setProduct(null);
    }
}

