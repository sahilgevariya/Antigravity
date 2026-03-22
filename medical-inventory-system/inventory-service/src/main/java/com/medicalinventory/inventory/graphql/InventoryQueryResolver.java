package com.medicalinventory.inventory.graphql;

import com.medicalinventory.common.dto.PagedResponse;
import com.medicalinventory.inventory.dto.ProductResponse;
import com.medicalinventory.inventory.dto.StockResponse;
import com.medicalinventory.inventory.service.ProductService;
import com.medicalinventory.inventory.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * GraphQL query resolver for Inventory Service.
 */
@Controller
@RequiredArgsConstructor
public class InventoryQueryResolver {

    private final ProductService productService;
    private final StockService stockService;

    @QueryMapping
    public ProductResponse productById(@Argument Long id) {
        return productService.getProductById(id);
    }

    @QueryMapping
    public ProductResponse productBySku(@Argument String sku) {
        return productService.getProductBySku(sku);
    }

    @QueryMapping
    public PagedResponse<ProductResponse> allProducts(@Argument int page, @Argument int size) {
        return productService.getAllProducts(page, size);
    }

    @QueryMapping
    public PagedResponse<ProductResponse> searchProducts(
            @Argument String query, @Argument int page, @Argument int size) {
        return productService.searchProducts(query, page, size);
    }

    @QueryMapping
    public PagedResponse<ProductResponse> productsByCategory(
            @Argument Long categoryId, @Argument int page, @Argument int size) {
        return productService.getProductsByCategory(categoryId, page, size);
    }

    @QueryMapping
    public List<StockResponse> stockByProduct(@Argument Long productId) {
        return stockService.getStockByProduct(productId);
    }

    @QueryMapping
    public List<StockResponse> lowStockItems() {
        return stockService.getLowStockItems();
    }

    @QueryMapping
    public List<StockResponse> expiringItems(@Argument int daysAhead) {
        return stockService.getExpiringItems(daysAhead);
    }
}
