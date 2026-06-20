package com.order_service.order_servie.DTO;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderAddressDto {

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

    // Getters & Setters
}

