package com.medicalinventory.payment.mapper;

import com.medicalinventory.payment.domain.Payment;
import com.medicalinventory.payment.dto.PaymentRequest;
import com.medicalinventory.payment.dto.PaymentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "transactionId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "paymentDate", ignore = true)
    @Mapping(target = "processedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Payment toPayment(PaymentRequest request);

    PaymentResponse toPaymentResponse(Payment payment);
}
