package com.medicalinventory.inventory.mapper;

import com.medicalinventory.inventory.domain.*;
import com.medicalinventory.inventory.dto.*;
import org.mapstruct.*;

/**
 * MapStruct mapper for Inventory domain ↔ DTO conversions.
 */
@Mapper(componentModel = "spring")
public interface InventoryMapper {

    // ── Product ────────────────────────────────────────────────

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "totalStock", ignore = true) // set manually
    ProductResponse toProductResponse(Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true) // set manually
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Product toProduct(ProductRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "sku", ignore = true)      // SKU is immutable after creation
    void updateProduct(ProductRequest request, @MappingTarget Product product);

    // ── Category ───────────────────────────────────────────────

    @Mapping(target = "parentId", source = "parent.id")
    @Mapping(target = "parentName", source = "parent.name")
    CategoryResponse toCategoryResponse(Category category);

    // ── Stock ──────────────────────────────────────────────────

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "productSku", source = "product.sku")
    @Mapping(target = "locationId", source = "location.id")
    @Mapping(target = "locationName", source = "location.name")
    StockResponse toStockResponse(Stock stock);
}
