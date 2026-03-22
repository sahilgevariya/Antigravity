package com.medicalinventory.supplier.client;

import com.medicalinventory.common.constant.AppConstants;
import com.medicalinventory.common.dto.ApiResponse;
import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * OpenFeign client to communicate with the Inventory Service.
 */
@FeignClient(name = "inventory-service", path = AppConstants.API_V1 + "/inventory")
public interface InventoryClient {

    @GetMapping("/products/{id}")
    ApiResponse<ProductDto> getProductById(@PathVariable("id") Long id);

    @Data
    class ProductDto {
        private Long id;
        private String name;
        private String sku;
        private int totalStock;
        private boolean active;
    }
}
