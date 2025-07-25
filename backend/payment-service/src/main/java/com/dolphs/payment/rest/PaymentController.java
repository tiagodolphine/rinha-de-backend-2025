package com.dolphs.payment.rest;

import com.dolphs.payment.domain.model.PaymentMessage;
import com.dolphs.payment.domain.model.PaymentResponse;
import com.dolphs.payment.domain.model.PaymentSummary;
import com.dolphs.payment.domain.model.PaymentTransaction;
import com.dolphs.payment.domain.service.PaymentService;
import com.dolphs.payment.worker.domain.consumer.PaymentMessageConsumer;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@RestController
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentMessageConsumer paymentProcessor;

    private final PaymentResponse response = new PaymentResponse();

    public PaymentController(PaymentService paymentService, PaymentMessageConsumer paymentProcessor) {
        this.paymentService = paymentService;
        this.paymentProcessor = paymentProcessor;
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

    @PostMapping("/process")
    public Mono<PaymentTransaction> createPayment(@RequestBody PaymentMessage payment) {
        return paymentProcessor.onMessage(payment);
    }
}
