package com.medicalinventory.inventory.service;

import com.medicalinventory.common.dto.PagedResponse;
import com.medicalinventory.inventory.dto.ProductRequest;
import com.medicalinventory.inventory.dto.ProductResponse;

public interface ProductService {

    ProductResponse createProduct(ProductRequest request);

    ProductResponse getProductById(Long id);

    ProductResponse getProductBySku(String sku);

    PagedResponse<ProductResponse> getAllProducts(int page, int size);

    PagedResponse<ProductResponse> searchProducts(String query, int page, int size);

    PagedResponse<ProductResponse> getProductsByCategory(Long categoryId, int page, int size);

    ProductResponse updateProduct(Long id, ProductRequest request);

    void deactivateProduct(Long id);
}
