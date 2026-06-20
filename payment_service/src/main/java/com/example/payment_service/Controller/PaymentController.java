package com.example.payment_service.Controller;

import com.example.payment_service.DTO.InitiatePaymentRequest;
import com.example.payment_service.DTO.InitiatePaymentResponse;
import com.example.payment_service.Service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * Called when user clicks "Proceed to Pay"
     */
    @PostMapping("/initiate")
    public ResponseEntity<InitiatePaymentResponse> initiatePayment(
            @RequestBody InitiatePaymentRequest request) {

        InitiatePaymentResponse response =
                paymentService.initiatePayment(request);

        return ResponseEntity.ok(response);
    }
}

