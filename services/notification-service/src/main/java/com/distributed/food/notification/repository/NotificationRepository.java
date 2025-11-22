package com.distributed.food.notification.repository;

import com.distributed.food.notification.model.NotificationRecord;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<NotificationRecord, UUID> {
    List<NotificationRecord> findByOrderId(UUID orderId);
}
