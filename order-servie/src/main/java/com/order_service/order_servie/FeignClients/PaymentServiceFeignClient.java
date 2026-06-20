package com.order_service.order_servie.FeignClients;

import com.order_service.order_servie.Config.FeignConfig;
import com.order_service.order_servie.DTO.InitiatePaymentRequest;
import com.order_service.order_servie.DTO.InitiatePaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="payment-service",configuration = FeignConfig.class)
public interface PaymentServiceFeignClient {

    @PostMapping("/payments/initate")
    public InitiatePaymentResponse initiatePayment(@RequestBody InitiatePaymentRequest initiatePaymentRequest);
}
