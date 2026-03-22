package com.medicalinventory.inventory.repository;

import com.medicalinventory.inventory.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    List<Stock> findByProductId(Long productId);

    List<Stock> findByLocationId(Long locationId);

    Optional<Stock> findByProductIdAndLocationIdAndBatchNumber(Long productId, Long locationId, String batchNumber);

    @Query("SELECT COALESCE(SUM(s.quantity), 0) FROM Stock s WHERE s.product.id = :productId")
    int getTotalStockByProductId(Long productId);

    @Query("SELECT s FROM Stock s WHERE s.product.id = :productId AND s.quantity > 0 ORDER BY s.expiryDate ASC NULLS LAST")
    List<Stock> findAvailableStockByProductId(Long productId);

    @Query("SELECT s FROM Stock s JOIN s.product p WHERE s.quantity <= p.minStockLevel AND p.active = true")
    List<Stock> findLowStockItems();

    @Query("SELECT s FROM Stock s WHERE s.expiryDate IS NOT NULL AND s.expiryDate <= :date AND s.quantity > 0")
    List<Stock> findExpiringBefore(LocalDate date);
}
