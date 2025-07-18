package com.dolphs.payment.worker.domain.processor;

import com.dolphs.payment.domain.model.Payment;
import com.dolphs.payment.domain.model.PaymentTransaction;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class PaymentProcessorClient {

    public static final Duration TIMEOUT = Duration.ofSeconds(2);

    private class Client {
        private final WebClient webClient;
        private final int id;

        public Client(WebClient webClient, int id) {
            this.webClient = webClient;
            this.id = id;
        }
    }

    private AtomicReference<Client> client;

    private final Client paymentProcessorDefault = new Client(WebClient.builder()
            .baseUrl("http://localhost:8001")
            .clientConnector(new ReactorClientHttpConnector(
                    HttpClient.create().responseTimeout(TIMEOUT)
            )).build()
            , 1);
    private final Client paymentProcessorFallback = new Client(WebClient.builder()
            .baseUrl("http://localhost:8002")
            .clientConnector(new ReactorClientHttpConnector(
                    HttpClient.create().responseTimeout(TIMEOUT)
            ))
            .build(), 2);

    public PaymentProcessorClient() {
        this.client = new AtomicReference<>(paymentProcessorDefault);
    }

    public Mono<Void> switchFallbackClient() {
        this.client.set(paymentProcessorFallback);
        return Mono.empty();
    }

    boolean healthCheck() {
        // Implement health check logic here, e.g., ping the service
        // For simplicity, we assume the default client is always healthy
        return true;
    }


    public void switchDefaultClient() {
        if (this.client.get().id == paymentProcessorDefault.id) {
            return;
        }
        this.client.set(paymentProcessorDefault);
    }

    public Mono<PaymentTransaction> process(Payment payment) {
        //POST /payments
        var currentClient = client.get();
        return currentClient.webClient
                .post()
                .uri("/payments")
                .bodyValue(payment)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> {
                            // Log the error or handle it as needed
                            return Mono.error(new RuntimeException("Failed to process payment: " + response.statusCode()));
                        })
                .toBodilessEntity()
                .map(r -> new PaymentTransaction(payment.getAmount(), currentClient.id, payment.getRequestedAt()))
                .onErrorResume(e -> {
                    // handle/log error, return fallback or propagate
                    return Mono.error(e);
                });

    }
}