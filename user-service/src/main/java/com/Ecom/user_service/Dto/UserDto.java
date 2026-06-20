package com.Ecom.user_service.Dto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    private Long id;
    private Long authUserId;   // comes from auth-service
    private String name;
    private String email;
    private String phone;
    private List<AddressDto> addresses; // nested DTO for addresses
}

