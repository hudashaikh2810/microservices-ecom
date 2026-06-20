package com.Ecom.user_service.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "addresses",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "street", "city", "state", "postal_code"})
        }
)@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String street;

    private String city;

    private String state;

    private String country;

    private String postalCode;
    private String tag;
    private String status;

    // In case you want multiple addresses for a user (One-to-Many)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;   // Assuming you have a User entity
}

