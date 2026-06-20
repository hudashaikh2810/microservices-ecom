package com.order_service.order_servie.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_address")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String phoneNumber;

    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String pincode;
    private String landmark;

    private String addressType; // HOME, WORK etc.
}

