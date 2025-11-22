package com.distributed.food.notification.controller;

import com.distributed.food.notification.model.NotificationRecord;
import com.distributed.food.notification.service.NotificationOrchestrator;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationOrchestrator orchestrator;

    public NotificationController(NotificationOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @GetMapping
    public List<NotificationRecord> list(@RequestParam(required = false) UUID orderId) {
        if (orderId != null) {
            return orchestrator.listByOrderId(orderId);
        }
        return orchestrator.listAll();
    }
}
