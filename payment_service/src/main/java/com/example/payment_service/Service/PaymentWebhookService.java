package com.example.payment_service.Service;
import com.example.payment_service.DTO.PaymentWebhookRequest;
import com.example.payment_service.Entity.Payment;
import com.example.payment_service.Enums.PaymentStatus;
import com.example.payment_service.Repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentWebhookService {

    private final PaymentRepository paymentRepository;

    public PaymentWebhookService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public void processWebhook(PaymentWebhookRequest request) {

        // 1️⃣ Idempotency check
        if (request.getWebhookEventId() != null &&
                paymentRepository.findByWebhookEventId(request.getWebhookEventId()).isPresent()) {
            return; // already processed
        }

        // 2️⃣ Find payment by gatewayOrderId
        Payment payment = paymentRepository
                .findByGatewayOrderId(request.getGatewayOrderId())
                .orElseThrow(() ->
                        new IllegalStateException("Payment not found for gateway order ID"));

        // 3️⃣ Update gateway payment ID
        payment.setGatewayPaymentId(request.getGatewayPaymentId());

        // 4️⃣ Update status based on event
        if ("payment.success".equalsIgnoreCase(request.getEvent())) {
            payment.setPaymentStatus(PaymentStatus.SUCCESS);
        } else if ("payment.failed".equalsIgnoreCase(request.getEvent())) {
            payment.setPaymentStatus(PaymentStatus.FAILED);
            payment.setFailureReason(request.getFailureReason());
        }

        // 5️⃣ Save webhook event ID for idempotency
        payment.setWebhookEventId(request.getWebhookEventId());

        paymentRepository.save(payment);

        // 6️⃣ TODO: Notify Order Service (next step)
    }
}

