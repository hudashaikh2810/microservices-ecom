package com.example.product_service.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "skus")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Sku {

    @Id
    private String id; // SKU-123-RED-L

    private String color;

    private String size;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private boolean isDefaultSku;

    @Column(nullable = false)
    private boolean isAvailable=true;

    /**
     * Many SKUs belong to one Product
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    /**
     * SKU owns images
     */
    @OneToMany(
            mappedBy = "sku",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Image> images = new ArrayList<>();

    public void addImage(Image image) {
        images.add(image);
        image.setSku(this);
    }

    public void removeImage(Image image) {
        images.remove(image);
        image.setSku(null);
    }
}

