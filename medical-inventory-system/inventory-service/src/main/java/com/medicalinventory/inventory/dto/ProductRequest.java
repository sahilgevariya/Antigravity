package com.medicalinventory.inventory.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    @NotBlank(message = "Product name is required")
    @Size(max = 255)
    private String name;

    @NotBlank(message = "SKU is required")
    @Size(max = 100)
    private String sku;

    @Size(max = 100)
    private String barcode;

    @Size(max = 1000)
    private String description;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.01", message = "Price must be positive")
    private BigDecimal unitPrice;

    @Size(max = 50)
    private String unitOfMeasurement;

    @Size(max = 255)
    private String manufacturer;

    private boolean requiresPrescription;
    private boolean controlled;

    @Min(0)
    private Integer minStockLevel;

    @Min(1)
    private Integer maxStockLevel;

    @Min(0)
    private Integer reorderPoint;

    @Min(1)
    private Integer expiryWarningDays;
}
