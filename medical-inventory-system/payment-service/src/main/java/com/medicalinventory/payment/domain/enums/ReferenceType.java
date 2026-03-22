package com.medicalinventory.payment.domain.enums;

/**
 * Types of references a payment can be attached to.
 */
public enum ReferenceType {
    ORDER,          // Incoming payment from a patient/clinic for an Order
    PURCHASE_ORDER, // Outgoing payment to a supplier for a Purchase Order
    SUBSCRIPTION,
    OTHER
}
