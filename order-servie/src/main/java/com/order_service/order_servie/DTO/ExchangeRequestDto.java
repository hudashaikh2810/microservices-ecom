package com.order_service.order_servie.DTO;

import com.order_service.order_servie.Enums.ExchangeStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRequestDto {

    private Long id;

    private String exchangeTrackingId;

    private Long oldOrderItemId;

    private Long originalOrderId;

    private Long replacementOrderId;

    private Long newOrderItemId;

    private Double priceDifference;

    private ExchangeStatus exchangeStatus;

    private LocalDateTime requestedAt;
    private LocalDateTime processedAt;
}

