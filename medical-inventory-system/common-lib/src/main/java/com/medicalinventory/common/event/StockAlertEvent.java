package com.medicalinventory.common.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Event published when stock drops below minimum level.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class StockAlertEvent extends BaseEvent {

    private Long productId;
    private String productName;
    private String sku;
    private int currentQuantity;
    private int minStockLevel;
    private String locationName;
}
