package com.dolphs.payment.repository;

import com.dolphs.payment.domain.model.PaymentTransaction;
import com.dolphs.payment.domain.model.Summary;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

public interface PaymentTransactionRepository extends ReactiveCrudRepository<PaymentTransaction, String> {
    @Query("""
                SELECT
                    COUNT(*) AS total_requests,
                    COALESCE(SUM(amount), 0) AS total_amount
                FROM payment_transaction
                WHERE (:from IS NULL OR timestamp >= :from)
                  AND (:to IS NULL OR timestamp <= :to)
                  AND processor_id = :processorId                
            """)
    Mono<Summary> getPaymentsSummary(OffsetDateTime from, OffsetDateTime to, int processorId);
}
