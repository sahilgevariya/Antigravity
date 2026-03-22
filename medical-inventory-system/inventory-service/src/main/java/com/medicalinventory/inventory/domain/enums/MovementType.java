package com.medicalinventory.inventory.domain.enums;

/**
 * Types of stock movements.
 */
public enum MovementType {
    STOCK_IN,
    STOCK_OUT,
    TRANSFER,
    ADJUSTMENT,
    RETURN,
    EXPIRED,
    DAMAGED
}
