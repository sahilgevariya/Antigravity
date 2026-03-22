package com.medicalinventory.inventory.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockRequest {

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Location ID is required")
    private Long locationId;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    @Size(max = 100)
    private String batchNumber;

    private LocalDate expiryDate;

    @Size(max = 500)
    private String reason;
}
