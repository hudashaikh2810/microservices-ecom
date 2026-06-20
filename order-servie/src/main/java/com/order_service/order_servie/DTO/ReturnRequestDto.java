package com.order_service.order_servie.DTO;

import com.order_service.order_servie.Enums.ReturnStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReturnRequestDto {

    private Long id;

    private String returnTrackingId;

    private Long orderItemId;

    private Long originalOrderId;

    private String reason;

    private ReturnStatus returnStatus;

    private Double refundAmount;

    private LocalDateTime requestedAt;
    private LocalDateTime pickupAt;
    private LocalDateTime pickedAt;
    private LocalDateTime processedAt;
}

