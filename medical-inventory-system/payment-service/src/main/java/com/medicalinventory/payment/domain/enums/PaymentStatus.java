package com.medicalinventory.payment.domain.enums;

/**
 * Status of a Payment.
 */
public enum PaymentStatus {
    PENDING,
    COMPLETED,
    FAILED,
    REFUNDED,
    CANCELLED
}
