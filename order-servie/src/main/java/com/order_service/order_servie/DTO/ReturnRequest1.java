package com.order_service.order_servie.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReturnRequest1 {
    private Long returnId;
    private Long orderId;
    private String orderTrackingId;
    private Long orderItemId;
    private String returnReason;
    private Integer quantityReturned;

}
