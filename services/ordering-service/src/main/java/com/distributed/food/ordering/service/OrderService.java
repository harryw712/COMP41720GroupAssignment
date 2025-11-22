package com.distributed.food.ordering.service;

import com.distributed.food.ordering.dto.CreateOrderRequest;
import com.distributed.food.ordering.dto.OrderResponse;
import com.distributed.food.ordering.dto.UpdateOrderStatusRequest;
import com.distributed.food.ordering.messaging.OrderEventPayload;
import com.distributed.food.ordering.messaging.OrderEventPublisher;
import com.distributed.food.ordering.model.OrderLineItem;
import com.distributed.food.ordering.model.OrderStatus;
import com.distributed.food.ordering.model.RestaurantOrder;
import com.distributed.food.ordering.repository.OrderRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final OrderRepository repository;
    private final OrderEventPublisher publisher;
    private final RestaurantCatalogClient catalogClient;

    public OrderService(OrderRepository repository,
                        OrderEventPublisher publisher,
                        RestaurantCatalogClient catalogClient) {
        this.repository = repository;
        this.publisher = publisher;
        this.catalogClient = catalogClient;
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        catalogClient.assertMenuIsAvailable(request.getRestaurantId());
        RestaurantOrder order = new RestaurantOrder();
        order.setCustomerEmail(request.getCustomerEmail());
        order.setCustomerName(request.getCustomerName());
        order.setRestaurantId(request.getRestaurantId());
        order.setItems(mapLineItems(request));
        order.setStatus(OrderStatus.CREATED);
        order.setTotalAmount(request.calculateTotalAmount());
        RestaurantOrder saved = repository.save(order);
        publisher.publishOrderCreated(saved);
        return toResponse(saved);
    }

    @Transactional
    public OrderResponse applyStatus(UUID orderId, UpdateOrderStatusRequest request) {
        RestaurantOrder order = repository.findById(orderId)
                .orElseThrow(() -> new IllegalStateException("Order not found: " + orderId));
        order.setStatus(request.status());
        order.setLatestStatusNote(request.note());
        RestaurantOrder updated = repository.save(order);
        publisher.publishOrderStatusUpdated(updated);
        return toResponse(updated);
    }

    @Transactional
    public void applyStatusFromMessage(UUID orderId, OrderStatus status, String note) {
        RestaurantOrder order = repository.findById(orderId)
                .orElseThrow(() -> new IllegalStateException("Order not found: " + orderId));
        order.setStatus(status);
        order.setLatestStatusNote(note);
        repository.save(order);
    }

    public List<OrderResponse> listOrders() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    public OrderResponse getOrder(UUID orderId) {
        return repository.findById(orderId)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalStateException("Order not found: " + orderId));
    }

    private List<OrderLineItem> mapLineItems(CreateOrderRequest request) {
        return request.getItems().stream().map(item -> {
            OrderLineItem lineItem = new OrderLineItem();
            lineItem.setMenuItemId(item.getMenuItemId());
            lineItem.setName(item.getName());
            lineItem.setQuantity(item.getQuantity());
            lineItem.setUnitPrice(item.getUnitPrice());
            return lineItem;
        }).toList();
    }

    private OrderResponse toResponse(RestaurantOrder order) {
        return new OrderResponse(
                order.getId(),
                order.getRestaurantId(),
                order.getCustomerEmail(),
                order.getCustomerName(),
                order.getStatus(),
                order.getLatestStatusNote(),
                order.getTotalAmount(),
                order.getCreatedAt(),
                order.getItems()
        );
    }
}
