package com.Ecom.user_service.Dto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressDto {

    private Long id;
    private String street;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private String tag;
    private String status;
}
