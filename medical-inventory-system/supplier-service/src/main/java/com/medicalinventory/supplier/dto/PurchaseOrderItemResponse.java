package com.medicalinventory.supplier.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderItemResponse {

    private Long id;
    private Long productId;
    private String productSku;
    private String productName;
    private int quantity;
    private BigDecimal unitPrice;
}
