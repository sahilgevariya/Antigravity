package com.medicalinventory.payment.service;

import com.medicalinventory.common.dto.PagedResponse;
import com.medicalinventory.payment.domain.enums.PaymentStatus;
import com.medicalinventory.payment.domain.enums.ReferenceType;
import com.medicalinventory.payment.dto.PaymentRequest;
import com.medicalinventory.payment.dto.PaymentResponse;

public interface PaymentService {

    PaymentResponse processPayment(PaymentRequest request, String username);

    PaymentResponse getPaymentById(Long id);

    PaymentResponse getPaymentByTransactionId(String transactionId);

    PagedResponse<PaymentResponse> getPaymentsByReference(String referenceNumber, ReferenceType referenceType, int page, int size);

    PagedResponse<PaymentResponse> getPaymentsByStatus(PaymentStatus status, int page, int size);

    PagedResponse<PaymentResponse> getAllPayments(int page, int size);

    PaymentResponse updatePaymentStatus(Long id, PaymentStatus status);
}
