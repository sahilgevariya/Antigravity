package com.medicalinventory.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockResponse {

    private Long id;
    private Long productId;
    private String productName;
    private String productSku;
    private Long locationId;
    private String locationName;
    private int quantity;
    private String batchNumber;
    private LocalDate expiryDate;
    private LocalDateTime lastRestocked;
    private LocalDateTime updatedAt;
}
