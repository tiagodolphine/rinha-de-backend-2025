package com.dolphs.payment.domain.model;

import org.springframework.data.annotation.Id;

public class PaymentMessage {
    private final double amount;
    private String correlationId;
    @Id
    private long id;

    public PaymentMessage(double amount, String correlationId) {
        this.amount = amount;
        this.correlationId = correlationId;
    }

    public double getAmount() {
        return amount;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}