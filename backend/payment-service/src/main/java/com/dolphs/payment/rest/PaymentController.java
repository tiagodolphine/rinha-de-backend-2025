package com.dolphs.payment.rest;

import com.dolphs.payment.domain.model.PaymentMessage;
import com.dolphs.payment.domain.model.PaymentResponse;
import com.dolphs.payment.domain.model.PaymentSummary;
import com.dolphs.payment.domain.service.PaymentService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@RestController
public class PaymentController {

    private final PaymentService paymentService;

    private final PaymentResponse response = new PaymentResponse();

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/payments")
    public Mono<PaymentResponse> createPayment(@RequestBody PaymentMessage payment, @RequestParam(required = false) boolean generate) {
        if(generate) payment.setCorrelationId(UUID.randomUUID().toString());
        return paymentService.createPayment(payment).map(p -> response);
    }

    @GetMapping("/payments-summary")
    public Mono<PaymentSummary> getPaymentSummary(@RequestParam(required = false) Optional<OffsetDateTime> from, @RequestParam(required = false) Optional<OffsetDateTime> to) {
        return paymentService.getPaymentSummary(from, to);
    }
}
