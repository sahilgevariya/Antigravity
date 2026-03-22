package com.medicalinventory.payment.dto;

import com.medicalinventory.payment.domain.enums.PaymentMethod;
import com.medicalinventory.payment.domain.enums.PaymentStatus;
import com.medicalinventory.payment.domain.enums.ReferenceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private Long id;
    private String transactionId;
    private String referenceNumber;
    private ReferenceType referenceType;
    private BigDecimal amount;
    private String currency;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private LocalDateTime paymentDate;
    private String notes;
    private String processedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
