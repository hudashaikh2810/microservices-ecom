package com.Ecom.cart_service.cart_service.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cart_items")
    public class CartItem {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String skuId;

        private int quantity;
        private Double pricePerUnit;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "cart_id")
        private Cart cart;
    }

