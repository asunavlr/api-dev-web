package com.kevinmatos.inventory.api;

import com.kevinmatos.inventory.domain.InventoryItem;
import com.kevinmatos.inventory.domain.InventoryReservation;
import com.kevinmatos.inventory.repository.InventoryItemRepository;
import com.kevinmatos.inventory.repository.InventoryReservationRepository;
import com.kevinmatos.inventory.service.InventoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    private final InventoryService inventory;
    private final InventoryItemRepository items;
    private final InventoryReservationRepository reservations;

    public InventoryController(
            InventoryService inventory,
            InventoryItemRepository items,
            InventoryReservationRepository reservations) {
        this.inventory = inventory;
        this.items = items;
        this.reservations = reservations;
    }

    @PutMapping("/items")
    public StockResponse setStock(@Valid @RequestBody StockRequest request) {
        InventoryItem item = inventory.setStock(request.sku(), request.availableQuantity());
        return new StockResponse(item.getSku(), item.getAvailableQuantity());
    }

    @GetMapping("/items/{sku}")
    public StockResponse getStock(@PathVariable String sku) {
        InventoryItem item = items.findBySku(sku)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SKU not found"));
        return new StockResponse(item.getSku(), item.getAvailableQuantity());
    }

    @GetMapping("/reservations/{orderId}")
    public ReservationResponse getReservation(@PathVariable String orderId) {
        InventoryReservation reservation = reservations.findByOrderId(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found"));
        return new ReservationResponse(
                reservation.getOrderId(), reservation.getSku(),
                reservation.getQuantity(), reservation.getStatus());
    }

    public record StockRequest(@NotBlank String sku, @Min(0) int availableQuantity) {}
    public record StockResponse(String sku, int availableQuantity) {}
    public record ReservationResponse(String orderId, String sku, int quantity, String status) {}
}
