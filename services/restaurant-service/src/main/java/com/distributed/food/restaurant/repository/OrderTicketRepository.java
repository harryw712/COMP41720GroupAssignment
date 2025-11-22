package com.distributed.food.restaurant.repository;

import com.distributed.food.restaurant.model.OrderTicket;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderTicketRepository extends JpaRepository<OrderTicket, UUID> {
    Optional<OrderTicket> findByOrderId(UUID orderId);
}
