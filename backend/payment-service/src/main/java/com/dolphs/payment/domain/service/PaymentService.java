package com.dolphs.payment.domain.service;

import com.dolphs.payment.domain.model.PaymentMessage;
import com.dolphs.payment.domain.model.PaymentSummary;
import com.dolphs.payment.domain.model.Summary;
import com.dolphs.payment.repository.PaymentRepository;
import com.dolphs.payment.repository.PaymentTransactionRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.Optional;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentTransactionRepository transactionRepository;

    public PaymentService(PaymentRepository paymentRepository, PaymentTransactionRepository transactionRepository) {
        this.paymentRepository = paymentRepository;
        this.transactionRepository = transactionRepository;
    }

    public Mono<PaymentMessage> createPayment(PaymentMessage payment) {
        return paymentRepository.save(payment);
    }

    public Mono<PaymentSummary> getPaymentSummary(Optional<OffsetDateTime> from, Optional<OffsetDateTime> to) {

        Mono<Summary> defaultProcessor = transactionRepository.getPaymentsSummary(from.orElse(OffsetDateTime.MIN), to.orElse(OffsetDateTime.now()), 1);
        Mono<Summary> fallbackProcessor = transactionRepository.getPaymentsSummary(from.orElse(OffsetDateTime.MIN), to.orElse(OffsetDateTime.now()), 2);

        return Mono.zip(defaultProcessor, fallbackProcessor)
                .map(tuple -> new PaymentSummary(tuple.getT1(), tuple.getT2()));
    }
}
