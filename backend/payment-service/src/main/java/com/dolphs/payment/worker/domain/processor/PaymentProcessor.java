package com.dolphs.payment.worker.domain.processor;

import com.dolphs.payment.domain.model.Payment;
import com.dolphs.payment.domain.model.PaymentMessage;
import com.dolphs.payment.domain.model.PaymentTransaction;
import com.dolphs.payment.repository.PaymentTransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

@Service
public class PaymentProcessor {

    private static final Logger log = LoggerFactory.getLogger(PaymentProcessor.class);
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final PaymentProcessorClient paymentProcessorClient;

    public PaymentProcessor(PaymentTransactionRepository paymentTransactionRepository, PaymentProcessorClient paymentProcessorClient) {
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.paymentProcessorClient = paymentProcessorClient;
    }

    public Mono<PaymentTransaction> process(PaymentMessage paymentMessage) {
        return paymentProcessorClient.process(new Payment(paymentMessage.getAmount(), paymentMessage.getCorrelationId(), OffsetDateTime.now()))
                .flatMap(paymentTransactionRepository::save)
                .onErrorResume(t -> onError(paymentMessage).flatMap(p -> Mono.error(t)))
                .doOnSuccess(t -> paymentProcessorClient.switchDefaultClient());
    }

    private Mono<Void> onError(PaymentMessage payment) {
        return paymentProcessorClient.switchFallbackClient()
                .doOnSuccess(p -> log.info("Switching to fallback client due to error"))
                //add to queue again
                .then(Mono.empty());
    }
}
