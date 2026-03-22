package com.medicalinventory.payment.repository;

import com.medicalinventory.payment.domain.Payment;
import com.medicalinventory.payment.domain.enums.PaymentStatus;
import com.medicalinventory.payment.domain.enums.ReferenceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByTransactionId(String transactionId);

    Page<Payment> findByReferenceNumberAndReferenceType(String referenceNumber, ReferenceType referenceType, Pageable pageable);

    Page<Payment> findByStatus(PaymentStatus status, Pageable pageable);
}
