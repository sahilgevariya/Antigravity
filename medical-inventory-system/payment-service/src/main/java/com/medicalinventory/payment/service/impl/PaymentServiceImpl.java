package com.medicalinventory.payment.service.impl;

import com.medicalinventory.common.dto.PagedResponse;
import com.medicalinventory.common.exception.BusinessRuleException;
import com.medicalinventory.common.exception.ResourceNotFoundException;
import com.medicalinventory.payment.domain.Payment;
import com.medicalinventory.payment.domain.enums.PaymentStatus;
import com.medicalinventory.payment.domain.enums.ReferenceType;
import com.medicalinventory.payment.dto.PaymentRequest;
import com.medicalinventory.payment.dto.PaymentResponse;
import com.medicalinventory.payment.mapper.PaymentMapper;
import com.medicalinventory.payment.repository.PaymentRepository;
import com.medicalinventory.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper mapper;

    @Override
    @Transactional
    public PaymentResponse processPayment(PaymentRequest request, String username) {
        log.info("Processing new payment for reference {}: {}", request.getReferenceType(), request.getReferenceNumber());

        // In a real system, this would integrate with a payment gateway (e.g., Stripe, PayPal, Bank API)
        // Here we simulate successful processing if amount is positive.
        
        Payment payment = mapper.toPayment(request);
        payment.setTransactionId(generateTransactionId());
        payment.setProcessedBy(username);
        
        // Simulating immediate success for this prototype
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setPaymentDate(LocalDateTime.now());

        payment = paymentRepository.save(payment);
        
        log.info("Payment processed successfully: Txn ID {}", payment.getTransactionId());
        
        // TODO: Publish Kafka event (PaymentProcessedEvent) in Phase 2 so Order/PO service can update its status automatically.
        
        return mapper.toPaymentResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .map(mapper::toPaymentResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByTransactionId(String transactionId) {
        return paymentRepository.findByTransactionId(transactionId)
                .map(mapper::toPaymentResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "transactionId", transactionId));
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<PaymentResponse> getPaymentsByReference(String referenceNumber, ReferenceType referenceType, int page, int size) {
        Page<Payment> paymentPage = paymentRepository.findByReferenceNumberAndReferenceType(
                referenceNumber, referenceType, 
                PageRequest.of(page, size, Sort.by("createdAt").descending()));
        return toPagedResponse(paymentPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<PaymentResponse> getPaymentsByStatus(PaymentStatus status, int page, int size) {
        Page<Payment> paymentPage = paymentRepository.findByStatus(
                status, 
                PageRequest.of(page, size, Sort.by("createdAt").descending()));
        return toPagedResponse(paymentPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<PaymentResponse> getAllPayments(int page, int size) {
        Page<Payment> paymentPage = paymentRepository.findAll(
                PageRequest.of(page, size, Sort.by("createdAt").descending()));
        return toPagedResponse(paymentPage);
    }

    @Override
    @Transactional
    public PaymentResponse updatePaymentStatus(Long id, PaymentStatus status) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));

        // State Machine validation
        if (payment.getStatus() == PaymentStatus.REFUNDED || payment.getStatus() == PaymentStatus.CANCELLED) {
            throw new BusinessRuleException("Cannot change status of a " + payment.getStatus() + " payment");
        }

        payment.setStatus(status);
        payment = paymentRepository.save(payment);

        log.info("Payment {} status updated to {}", payment.getTransactionId(), status);

        // TODO: Publish Kafka event (PaymentStatusChangedEvent) to notify Order/Supplier services

        return mapper.toPaymentResponse(payment);
    }

    // ── Helpers ──────────────────────────────────────────────────

    private String generateTransactionId() {
        return "TXN-" + LocalDateTime.now().getYear() + "-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();
    }

    private PagedResponse<PaymentResponse> toPagedResponse(Page<Payment> page) {
        List<PaymentResponse> content = page.getContent().stream()
                .map(mapper::toPaymentResponse)
                .toList();
        return PagedResponse.<PaymentResponse>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }
}
