package com.medicalinventory.order.domain.enums;

/**
 * Lifecycle states of an Order.
 */
public enum OrderStatus {
    PENDING,
    APPROVED,
    REJECTED,
    PARTIALLY_FULFILLED,
    FULFILLED,
    CANCELLED
}
