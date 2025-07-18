package com.dolphs.payment.worker.domain.consumer;

import com.dolphs.payment.domain.model.PaymentMessage;
import com.dolphs.payment.repository.PaymentRepository;
import com.dolphs.payment.worker.domain.processor.PaymentProcessor;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class PaymentMessageConsumer {

    private static final Logger log = LoggerFactory.getLogger(PaymentMessageConsumer.class);
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final PaymentProcessor paymentProcessor;
    private final PaymentRepository paymentRepository;
    private final TransactionalOperator transactionalOperator;
    private AtomicReference<Disposable> subscribed = new AtomicReference<>();

    public PaymentMessageConsumer(PaymentProcessor paymentProcessor, PaymentRepository paymentRepository, TransactionalOperator transactionalOperator) {
        this.paymentProcessor = paymentProcessor;
        this.paymentRepository = paymentRepository;
        this.transactionalOperator = transactionalOperator;
        ExecutorService virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor();
        virtualThreadExecutor.submit(() -> {
            while (running.get()) {
                Long block = fetchMessages()
                        .count()
                        .block();
                if (block != null && block > 0) {
                    log.info("Fetched {} messages to process", block);
                } else {
                    log.info("No messages to process...");
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    @PreDestroy
    public void onShutdown() {
        log.info("Spring context shutting down, stopping consumer...");
        running.set(false);
        subscribed.get().dispose();
    }

    public Flux<PaymentMessage> fetchMessages() {
        return paymentRepository
                .findNextChunkToProcess(10)
                .as(transactionalOperator::transactional) // Ensures both operations are in a transaction
                .flatMap(this::onMessage);
    }

    public Mono<PaymentMessage> onMessage(PaymentMessage paymentMessage) {
        log.info("Received PaymentMessage {}", paymentMessage);
        return paymentProcessor.process(paymentMessage)
                .flatMap(p -> {
                    log.info("Payment processed successfully: {}", p);
                    return paymentRepository.delete(paymentMessage);
                })
                .map(p -> paymentMessage)
                .doOnError(e -> log.error("Error processing payment message: {}", paymentMessage, e));

    }
}