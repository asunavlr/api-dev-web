package com.kevinmatos.orders.repository;

import com.kevinmatos.orders.domain.OrderEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {}
