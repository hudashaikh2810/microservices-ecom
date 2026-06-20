package com.order_service.order_servie.Service;

import com.order_service.order_servie.DTO.InitiatePaymentRequest;
import com.order_service.order_servie.DTO.InitiatePaymentResponse;
import com.order_service.order_servie.FeignClients.PaymentServiceFeignClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceClient {
    @Autowired
    private PaymentServiceFeignClient paymentServiceFeignClient;

    @CircuitBreaker(name = "payment-service", fallbackMethod = "initatePaymentRequestFailed")
    public InitiatePaymentResponse initiatePaymentRequest(InitiatePaymentRequest paymentRequest) {
        return paymentServiceFeignClient.initiatePayment(paymentRequest);
    }

    public InitiatePaymentResponse initiatePaymentRequestFailed(InitiatePaymentRequest request, Throwable t) {
        return null;
    }

}
