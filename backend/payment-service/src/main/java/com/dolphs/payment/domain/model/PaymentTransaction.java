package com.dolphs.payment.domain.model;

import org.springframework.data.annotation.Id;

import java.time.OffsetDateTime;

public class PaymentTransaction {
    private final double amount;
    private final int processorId;
    private final OffsetDateTime timestamp;
    @Id
    private long id;

    public PaymentTransaction(double amount, int processorId, OffsetDateTime timestamp) {
        this.amount = amount;
        this.processorId = processorId;
        this.timestamp = timestamp;
    }

    public double getAmount() {
        return amount;
    }

    public int getProcessorId() {
        return processorId;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }
}