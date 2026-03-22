package com.medicalinventory.supplier.controller;

import com.medicalinventory.common.constant.AppConstants;
import com.medicalinventory.common.dto.ApiResponse;
import com.medicalinventory.common.dto.PagedResponse;
import com.medicalinventory.supplier.dto.SupplierRequest;
import com.medicalinventory.supplier.dto.SupplierResponse;
import com.medicalinventory.supplier.service.SupplierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(AppConstants.API_V1 + "/suppliers")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Suppliers", description = "Vendor and Supplier management")
public class SupplierController {

    private final SupplierService supplierService;

    @PostMapping
    @Operation(summary = "Create a new supplier")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SupplierResponse>> createSupplier(@Valid @RequestBody SupplierRequest request) {
        SupplierResponse response = supplierService.createSupplier(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Supplier created successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get supplier by ID")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    public ResponseEntity<ApiResponse<SupplierResponse>> getSupplierById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(supplierService.getSupplierById(id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update supplier details")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SupplierResponse>> updateSupplier(
            @PathVariable Long id, @Valid @RequestBody SupplierRequest request) {
        SupplierResponse response = supplierService.updateSupplier(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Supplier updated successfully"));
    }

    @GetMapping
    @Operation(summary = "Get all suppliers")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    public ResponseEntity<ApiResponse<PagedResponse<SupplierResponse>>> getAllSuppliers(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
        return ResponseEntity.ok(ApiResponse.success(
                supplierService.getAllSuppliers(page, size, search)));
    }

    @GetMapping("/active")
    @Operation(summary = "Get active suppliers only")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    public ResponseEntity<ApiResponse<PagedResponse<SupplierResponse>>> getActiveSuppliers(
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
        return ResponseEntity.ok(ApiResponse.success(
                supplierService.getActiveSuppliers(page, size)));
    }
}
