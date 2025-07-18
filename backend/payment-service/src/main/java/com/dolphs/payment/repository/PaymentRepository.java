package com.dolphs.payment.repository;

import com.dolphs.payment.domain.model.PaymentMessage;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface PaymentRepository extends ReactiveCrudRepository<PaymentMessage, String> {
    int CHUNK_SIZE = 10; // Example constant

    @Query("SELECT * FROM payment_message ORDER BY id LIMIT :chunkSize FOR UPDATE SKIP LOCKED")
    Flux<PaymentMessage> findNextChunkToProcess(int chunkSize);
}