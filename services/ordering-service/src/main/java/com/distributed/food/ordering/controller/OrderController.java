package com.distributed.food.ordering.controller;

import com.distributed.food.ordering.dto.CreateOrderRequest;
import com.distributed.food.ordering.dto.OrderResponse;
import com.distributed.food.ordering.dto.UpdateOrderStatusRequest;
import com.distributed.food.ordering.service.OrderService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse create(@Valid @RequestBody CreateOrderRequest request) {
        return orderService.createOrder(request);
    }

    @GetMapping
    public List<OrderResponse> list() {
        return orderService.listOrders();
    }

    @GetMapping("/{orderId}")
    public OrderResponse get(@PathVariable UUID orderId) {
        return orderService.getOrder(orderId);
    }

    @PutMapping("/{orderId}/status")
    public OrderResponse updateStatus(@PathVariable UUID orderId,
                                      @Valid @RequestBody UpdateOrderStatusRequest request) {
        return orderService.applyStatus(orderId, request);
    }
}
