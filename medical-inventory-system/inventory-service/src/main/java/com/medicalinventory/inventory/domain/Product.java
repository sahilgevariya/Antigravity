package com.medicalinventory.inventory.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Product entity — core medical inventory item.
 */
@Entity
@Table(name = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String sku;

    @Column(unique = true, length = 100)
    private String barcode;

    @Column(length = 1000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "unit_of_measurement", nullable = false, length = 50)
    @Builder.Default
    private String unitOfMeasurement = "UNIT";

    @Column(length = 255)
    private String manufacturer;

    @Column(name = "requires_prescription", nullable = false)
    @Builder.Default
    private boolean requiresPrescription = false;

    @Column(name = "is_controlled", nullable = false)
    @Builder.Default
    private boolean controlled = false;

    @Column(name = "min_stock_level", nullable = false)
    @Builder.Default
    private int minStockLevel = 10;

    @Column(name = "max_stock_level", nullable = false)
    @Builder.Default
    private int maxStockLevel = 1000;

    @Column(name = "reorder_point", nullable = false)
    @Builder.Default
    private int reorderPoint = 20;

    @Column(name = "expiry_warning_days", nullable = false)
    @Builder.Default
    private int expiryWarningDays = 30;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
