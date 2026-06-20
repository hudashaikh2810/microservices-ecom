package com.example.payment_service.Service;

import com.example.payment_service.DTO.InitiatePaymentRequest;
import com.example.payment_service.DTO.InitiatePaymentResponse;
import com.example.payment_service.Entity.Payment;
import com.example.payment_service.PaymentGateway.PaymentGatewayClient;
import com.example.payment_service.Record.GatewayOrderResponse;
import com.example.payment_service.Repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentGatewayClient gatewayClient;

    public PaymentService(PaymentRepository paymentRepository,
                          PaymentGatewayClient gatewayClient) {
        this.paymentRepository = paymentRepository;
        this.gatewayClient = gatewayClient;
    }

    @Transactional
    public InitiatePaymentResponse initiatePayment(InitiatePaymentRequest request) {

        // 1️⃣ Prevent duplicate payment initiation
        paymentRepository.findByOrderId(request.getOrderId())
                .ifPresent(p -> {
                    throw new IllegalStateException(
                            "Payment already initiated for order " + request.getOrderId()
                    );
                });

        // 2️⃣ Create internal payment
        Payment payment = new Payment();
        payment.setOrderId(request.getOrderId());
        payment.setAmount(request.getAmount());
        payment.setCurrency(request.getCurrency());
        payment.setPaymentType(request.getPaymentType());

        payment = paymentRepository.save(payment);

        // 3️⃣ Create gateway order (NO payment yet)
        GatewayOrderResponse gatewayOrder =
                gatewayClient.createOrder(request.getAmount(), request.getCurrency());

        // 4️⃣ Persist gateway reference
        payment.setGatewayOrderId(gatewayOrder.gatewayOrderId());
        try{
            paymentRepository.save(payment);

        }
        catch(Exception e)
        {
            throw new RuntimeException("There is something wrong cannot proceed");
        }

        // 5️⃣ Return response to frontend
        InitiatePaymentResponse response = new InitiatePaymentResponse();
        response.setPaymentId(payment.getPaymentId());
        response.setGatewayOrderId(gatewayOrder.gatewayOrderId());
        response.setAmount(payment.getAmount());
        response.setCurrency(payment.getCurrency());

        return response;
    }
}
