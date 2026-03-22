package com.medicalinventory.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {

    private Long id;
    private Long productId;
    private String productSku;
    private String productName;
    private int quantityRequested;
    private int quantityFulfilled;
    private String notes;
}
