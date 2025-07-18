package com.dolphs.payment.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.OffsetDateTime;

public class Payment {
    private final double amount;
    private final String correlationId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private final OffsetDateTime requestedAt;

    public Payment(double amount, String correlationId, OffsetDateTime requestedAt) {
        this.amount = amount;
        this.correlationId = correlationId;
        this.requestedAt = requestedAt;
    }

    public double getAmount() {
        return amount;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public OffsetDateTime getRequestedAt() {
        return requestedAt;
    }
}