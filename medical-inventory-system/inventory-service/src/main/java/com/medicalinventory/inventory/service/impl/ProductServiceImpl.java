package com.medicalinventory.inventory.service.impl;

import com.medicalinventory.common.dto.PagedResponse;
import com.medicalinventory.common.exception.DuplicateResourceException;
import com.medicalinventory.common.exception.ResourceNotFoundException;
import com.medicalinventory.inventory.domain.Category;
import com.medicalinventory.inventory.domain.Product;
import com.medicalinventory.inventory.dto.ProductRequest;
import com.medicalinventory.inventory.dto.ProductResponse;
import com.medicalinventory.inventory.mapper.InventoryMapper;
import com.medicalinventory.inventory.repository.CategoryRepository;
import com.medicalinventory.inventory.repository.ProductRepository;
import com.medicalinventory.inventory.repository.StockRepository;
import com.medicalinventory.inventory.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final StockRepository stockRepository;
    private final InventoryMapper mapper;

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        if (productRepository.existsBySku(request.getSku())) {
            throw new DuplicateResourceException("Product", "sku", request.getSku());
        }
        if (request.getBarcode() != null && productRepository.existsByBarcode(request.getBarcode())) {
            throw new DuplicateResourceException("Product", "barcode", request.getBarcode());
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        Product product = mapper.toProduct(request);
        product.setCategory(category);
        product = productRepository.save(product);

        log.info("Product created: {} (SKU: {})", product.getName(), product.getSku());
        return enrichWithStock(mapper.toProductResponse(product));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "products", key = "#id")
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        return enrichWithStock(mapper.toProductResponse(product));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "products", key = "'sku:' + #sku")
    public ProductResponse getProductBySku(String sku) {
        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "sku", sku));
        return enrichWithStock(mapper.toProductResponse(product));
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ProductResponse> getAllProducts(int page, int size) {
        Page<Product> productPage = productRepository.findByActiveTrue(
                PageRequest.of(page, size, Sort.by("name")));
        return toPagedResponse(productPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ProductResponse> searchProducts(String query, int page, int size) {
        Page<Product> productPage = productRepository.searchProducts(query,
                PageRequest.of(page, size, Sort.by("name")));
        return toPagedResponse(productPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ProductResponse> getProductsByCategory(Long categoryId, int page, int size) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category", "id", categoryId);
        }
        Page<Product> productPage = productRepository.findByCategoryIdAndActiveTrue(categoryId,
                PageRequest.of(page, size, Sort.by("name")));
        return toPagedResponse(productPage);
    }

    @Override
    @Transactional
    @CacheEvict(value = "products", key = "#id")
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        if (request.getCategoryId() != null && !request.getCategoryId().equals(product.getCategory().getId())) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));
            product.setCategory(category);
        }

        mapper.updateProduct(request, product);
        product = productRepository.save(product);

        log.info("Product updated: {} (SKU: {})", product.getName(), product.getSku());
        return enrichWithStock(mapper.toProductResponse(product));
    }

    @Override
    @Transactional
    @CacheEvict(value = "products", key = "#id")
    public void deactivateProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        product.setActive(false);
        productRepository.save(product);
        log.info("Product deactivated: {} (SKU: {})", product.getName(), product.getSku());
    }

    // ── Helpers ─────────────────────────────────────────────────

    private ProductResponse enrichWithStock(ProductResponse response) {
        int totalStock = stockRepository.getTotalStockByProductId(response.getId());
        response.setTotalStock(totalStock);
        return response;
    }

    private PagedResponse<ProductResponse> toPagedResponse(Page<Product> productPage) {
        List<ProductResponse> content = productPage.getContent().stream()
                .map(p -> enrichWithStock(mapper.toProductResponse(p)))
                .toList();

        return PagedResponse.<ProductResponse>builder()
                .content(content)
                .page(productPage.getNumber())
                .size(productPage.getSize())
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .first(productPage.isFirst())
                .last(productPage.isLast())
                .build();
    }
}
