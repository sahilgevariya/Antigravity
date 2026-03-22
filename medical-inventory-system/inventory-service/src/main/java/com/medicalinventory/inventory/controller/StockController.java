package com.medicalinventory.inventory.controller;

import com.medicalinventory.common.constant.AppConstants;
import com.medicalinventory.common.dto.ApiResponse;
import com.medicalinventory.inventory.dto.StockRequest;
import com.medicalinventory.inventory.dto.StockResponse;
import com.medicalinventory.inventory.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(AppConstants.API_V1 + "/inventory/stock")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Stock", description = "Stock management — add, remove, and monitor inventory levels")
public class StockController {

    private final StockService stockService;

    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    @Operation(summary = "Add stock (restock)")
    public ResponseEntity<ApiResponse<StockResponse>> addStock(
            @Valid @RequestBody StockRequest request, Authentication auth) {
        StockResponse response = stockService.addStock(request, auth.getName());
        return ResponseEntity.ok(ApiResponse.success(response, "Stock added successfully"));
    }

    @PostMapping("/remove")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST', 'DOCTOR')")
    @Operation(summary = "Remove stock (dispense/use)")
    public ResponseEntity<ApiResponse<StockResponse>> removeStock(
            @Valid @RequestBody StockRequest request, Authentication auth) {
        StockResponse response = stockService.removeStock(request, auth.getName());
        return ResponseEntity.ok(ApiResponse.success(response, "Stock removed successfully"));
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "Get stock levels for a product across all locations")
    public ResponseEntity<ApiResponse<List<StockResponse>>> getStockByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.success(stockService.getStockByProduct(productId)));
    }

    @GetMapping("/location/{locationId}")
    @Operation(summary = "Get stock levels at a location")
    public ResponseEntity<ApiResponse<List<StockResponse>>> getStockByLocation(@PathVariable Long locationId) {
        return ResponseEntity.ok(ApiResponse.success(stockService.getStockByLocation(locationId)));
    }

    @GetMapping("/low-stock")
    @Operation(summary = "Get items below minimum stock level")
    public ResponseEntity<ApiResponse<List<StockResponse>>> getLowStockItems() {
        return ResponseEntity.ok(ApiResponse.success(stockService.getLowStockItems()));
    }

    @GetMapping("/expiring")
    @Operation(summary = "Get items expiring within N days")
    public ResponseEntity<ApiResponse<List<StockResponse>>> getExpiringItems(
            @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(ApiResponse.success(stockService.getExpiringItems(days)));
    }

    @GetMapping("/total/{productId}")
    @Operation(summary = "Get total stock count for a product")
    public ResponseEntity<ApiResponse<Integer>> getTotalStock(@PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.success(stockService.getTotalStock(productId)));
    }
}
