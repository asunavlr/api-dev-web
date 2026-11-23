package com.kevinmatos.orders.api;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateOrderRequest(
        @NotBlank @Size(max = 120) String customerId,
        @NotBlank @Size(max = 120) String sku,
        @NotNull @Min(1) Integer quantity) {}
