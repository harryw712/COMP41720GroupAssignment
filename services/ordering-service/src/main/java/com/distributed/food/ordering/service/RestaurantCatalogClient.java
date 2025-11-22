package com.distributed.food.ordering.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
public class RestaurantCatalogClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestaurantCatalogClient.class);

    private final RestClient restClient;

    public RestaurantCatalogClient(RestClient.Builder builder,
                                   @Value("${app.restaurant-service-base-url:http://localhost:8081}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    @CircuitBreaker(name = "restaurantCatalog", fallbackMethod = "fallback")
    @Retry(name = "restaurantCatalog")
    public void assertMenuIsAvailable(UUID restaurantId) {
        restClient.get()
                .uri("/internal/availability/{restaurantId}", restaurantId)
                .retrieve()
                .toBodilessEntity();
    }

    void fallback(UUID restaurantId, Throwable throwable) {
        LOGGER.error("Restaurant catalog unavailable for {}", restaurantId, throwable);
        throw new IllegalStateException("Restaurant catalog unavailable", unwrap(throwable));
    }

    private Throwable unwrap(Throwable throwable) {
        if (throwable instanceof RestClientResponseException responseException) {
            return new IllegalStateException(responseException.getStatusText(), responseException);
        }
        return throwable;
    }
}
