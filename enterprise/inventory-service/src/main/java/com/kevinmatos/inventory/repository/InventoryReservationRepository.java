package com.kevinmatos.inventory.repository;

import com.kevinmatos.inventory.domain.InventoryReservation;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryReservationRepository extends JpaRepository<InventoryReservation, String> {
    Optional<InventoryReservation> findByOrderId(String orderId);
}
