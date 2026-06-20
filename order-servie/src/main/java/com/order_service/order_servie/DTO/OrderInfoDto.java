package com.order_service.order_servie.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderInfoDto {

    private Long id;

    private Long orderId;

    private String updateType;

    private String updateDetails;

    private LocalDateTime updatedAt;
}

