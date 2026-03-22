package com.medicalinventory.supplier.controller;

import com.medicalinventory.common.constant.AppConstants;
import com.medicalinventory.common.dto.ApiResponse;
import com.medicalinventory.common.dto.PagedResponse;
import com.medicalinventory.supplier.domain.enums.PurchaseOrderStatus;
import com.medicalinventory.supplier.dto.PurchaseOrderRequest;
import com.medicalinventory.supplier.dto.PurchaseOrderResponse;
import com.medicalinventory.supplier.service.PurchaseOrderService;
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
@RequestMapping(AppConstants.API_V1 + "/purchase-orders")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Purchase Orders", description = "Purchase Order management")
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @PostMapping
    @Operation(summary = "Create a new Purchase Order")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>> createPurchaseOrder(
            @Valid @RequestBody PurchaseOrderRequest request, Authentication authentication) {
        PurchaseOrderResponse response = purchaseOrderService.createPurchaseOrder(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Purchase Order created successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get PO by ID")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>> getPurchaseOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(purchaseOrderService.getPurchaseOrderById(id)));
    }

    @GetMapping("/number/{poNumber}")
    @Operation(summary = "Get PO by PO Number")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>> getPurchaseOrderByNumber(@PathVariable String poNumber) {
        return ResponseEntity.ok(ApiResponse.success(purchaseOrderService.getPurchaseOrderByPoNumber(poNumber)));
    }

    @GetMapping("/supplier/{supplierId}")
    @Operation(summary = "Get Purchase Orders by Supplier")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    public ResponseEntity<ApiResponse<PagedResponse<PurchaseOrderResponse>>> getPurchaseOrdersBySupplier(
            @PathVariable Long supplierId,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
        return ResponseEntity.ok(ApiResponse.success(
                purchaseOrderService.getPurchaseOrdersBySupplier(supplierId, page, size)));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get Purchase Orders by Status")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    public ResponseEntity<ApiResponse<PagedResponse<PurchaseOrderResponse>>> getPurchaseOrdersByStatus(
            @PathVariable PurchaseOrderStatus status,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
        return ResponseEntity.ok(ApiResponse.success(
                purchaseOrderService.getPurchaseOrdersByStatus(status, page, size)));
    }

    @GetMapping
    @Operation(summary = "Get all Purchase Orders")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    public ResponseEntity<ApiResponse<PagedResponse<PurchaseOrderResponse>>> getAllPurchaseOrders(
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
        return ResponseEntity.ok(ApiResponse.success(purchaseOrderService.getAllPurchaseOrders(page, size)));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update PO Status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>> updatePurchaseOrderStatus(
            @PathVariable Long id,
            @RequestParam PurchaseOrderStatus status) {
        PurchaseOrderResponse response = purchaseOrderService.updatePurchaseOrderStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success(response, "PO Status updated successfully"));
    }
}
