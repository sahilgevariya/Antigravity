package com.medicalinventory.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private Long id;
    private String name;
    private String sku;
    private String barcode;
    private String description;
    private Long categoryId;
    private String categoryName;
    private BigDecimal unitPrice;
    private String unitOfMeasurement;
    private String manufacturer;
    private boolean requiresPrescription;
    private boolean controlled;
    private int minStockLevel;
    private int maxStockLevel;
    private int reorderPoint;
    private int expiryWarningDays;
    private int totalStock;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
