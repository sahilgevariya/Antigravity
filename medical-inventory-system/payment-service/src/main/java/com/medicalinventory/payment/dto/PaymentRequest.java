package com.medicalinventory.payment.dto;

import com.medicalinventory.payment.domain.enums.PaymentMethod;
import com.medicalinventory.payment.domain.enums.ReferenceType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {

    @NotBlank(message = "Reference Number is required")
    @Size(max = 100)
    private String referenceNumber;

    @NotNull(message = "Reference Type is required")
    private ReferenceType referenceType;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private BigDecimal amount;

    private String currency = "USD";

    @NotNull(message = "Payment Method is required")
    private PaymentMethod paymentMethod;

    @Size(max = 500)
    private String notes;
}
