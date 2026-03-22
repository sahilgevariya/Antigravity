package com.medicalinventory.payment.controller;

import com.medicalinventory.common.constant.AppConstants;
import com.medicalinventory.common.dto.ApiResponse;
import com.medicalinventory.common.dto.PagedResponse;
import com.medicalinventory.payment.domain.enums.PaymentStatus;
import com.medicalinventory.payment.domain.enums.ReferenceType;
import com.medicalinventory.payment.dto.PaymentRequest;
import com.medicalinventory.payment.dto.PaymentResponse;
import com.medicalinventory.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(AppConstants.API_V1 + "/payments")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Payments", description = "Payment transaction management")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @Operation(summary = "Process a new payment")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST', 'CLINICIAN')")
    public ResponseEntity<ApiResponse<PaymentResponse>> processPayment(
            @Valid @RequestBody PaymentRequest request, Authentication authentication) {
        PaymentResponse response = paymentService.processPayment(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Payment processed successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment by ID")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST', 'CLINICIAN')")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getPaymentById(id)));
    }

    @GetMapping("/transaction/{transactionId}")
    @Operation(summary = "Get payment by Transaction ID")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST', 'CLINICIAN')")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentByTransactionId(@PathVariable String transactionId) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getPaymentByTransactionId(transactionId)));
    }

    @GetMapping("/reference/{type}/{number}")
    @Operation(summary = "Get payments by Reference Number and Type")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST', 'CLINICIAN')")
    public ResponseEntity<ApiResponse<PagedResponse<PaymentResponse>>> getPaymentsByReference(
            @PathVariable ReferenceType type,
            @PathVariable String number,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
        return ResponseEntity.ok(ApiResponse.success(
                paymentService.getPaymentsByReference(number, type, page, size)));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get payments by Status")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    public ResponseEntity<ApiResponse<PagedResponse<PaymentResponse>>> getPaymentsByStatus(
            @PathVariable PaymentStatus status,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
        return ResponseEntity.ok(ApiResponse.success(
                paymentService.getPaymentsByStatus(status, page, size)));
    }

    @GetMapping
    @Operation(summary = "Get all payments")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    public ResponseEntity<ApiResponse<PagedResponse<PaymentResponse>>> getAllPayments(
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getAllPayments(page, size)));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update payment status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PaymentResponse>> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam PaymentStatus status) {
        PaymentResponse response = paymentService.updatePaymentStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success(response, "Payment status updated successfully"));
    }
}
