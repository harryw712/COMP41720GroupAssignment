package com.distributed.food.restaurant.controller;

import com.distributed.food.restaurant.dto.KitchenStatusUpdateRequest;
import com.distributed.food.restaurant.model.OrderTicket;
import com.distributed.food.restaurant.service.KitchenWorkflowService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/kitchen")
public class KitchenController {

    private final KitchenWorkflowService kitchenWorkflowService;

    public KitchenController(KitchenWorkflowService kitchenWorkflowService) {
        this.kitchenWorkflowService = kitchenWorkflowService;
    }

    @PostMapping("/{orderId}/status")
    public OrderTicket advance(@PathVariable UUID orderId,
                               @Valid @RequestBody KitchenStatusUpdateRequest request) {
        return kitchenWorkflowService.updateStatus(orderId, request);
    }
}
