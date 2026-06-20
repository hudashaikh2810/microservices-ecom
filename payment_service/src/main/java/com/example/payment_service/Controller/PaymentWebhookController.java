package com.example.payment_service.Controller;

import com.example.payment_service.DTO.PaymentWebhookRequest;
import com.example.payment_service.Service.PaymentWebhookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhooks")
public class PaymentWebhookController {

    private final PaymentWebhookService webhookService;

    public PaymentWebhookController(PaymentWebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @PostMapping("/payments")
    public ResponseEntity<Void> handlePaymentWebhook(
            @RequestBody PaymentWebhookRequest request) {

        webhookService.processWebhook(request);

        return ResponseEntity.ok().build();
    }
}

