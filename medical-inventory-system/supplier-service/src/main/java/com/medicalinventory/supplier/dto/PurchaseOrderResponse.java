package com.medicalinventory.supplier.dto;

import com.medicalinventory.supplier.domain.enums.PurchaseOrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderResponse {

    private Long id;
    private String poNumber;
    private SupplierResponse supplier;
    private PurchaseOrderStatus status;
    private BigDecimal totalAmount;
    private LocalDate expectedDate;
    private String notes;
    private String createdBy;
    private List<PurchaseOrderItemResponse> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
