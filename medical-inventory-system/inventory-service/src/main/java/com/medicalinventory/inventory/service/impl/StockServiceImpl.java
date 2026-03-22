package com.medicalinventory.inventory.service.impl;

import com.medicalinventory.common.exception.InsufficientStockException;
import com.medicalinventory.common.exception.ResourceNotFoundException;
import com.medicalinventory.inventory.domain.*;
import com.medicalinventory.inventory.domain.enums.MovementType;
import com.medicalinventory.inventory.dto.StockRequest;
import com.medicalinventory.inventory.dto.StockResponse;
import com.medicalinventory.inventory.event.StockEventPublisher;
import com.medicalinventory.inventory.mapper.InventoryMapper;
import com.medicalinventory.inventory.repository.*;
import com.medicalinventory.inventory.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;
    private final ProductRepository productRepository;
    private final LocationRepository locationRepository;
    private final StockMovementRepository movementRepository;
    private final StockEventPublisher eventPublisher;
    private final InventoryMapper mapper;

    @Override
    @Transactional
    @CacheEvict(value = "products", key = "#request.productId")
    public StockResponse addStock(StockRequest request, String performedBy) {
        Product product = findProduct(request.getProductId());
        Location location = findLocation(request.getLocationId());

        // Find or create stock record
        Stock stock = stockRepository
                .findByProductIdAndLocationIdAndBatchNumber(
                        request.getProductId(), request.getLocationId(), request.getBatchNumber())
                .orElseGet(() -> Stock.builder()
                        .product(product)
                        .location(location)
                        .batchNumber(request.getBatchNumber())
                        .expiryDate(request.getExpiryDate())
                        .quantity(0)
                        .build());

        stock.setQuantity(stock.getQuantity() + request.getQuantity());
        stock.setLastRestocked(LocalDateTime.now());
        if (request.getExpiryDate() != null) {
            stock.setExpiryDate(request.getExpiryDate());
        }
        stock = stockRepository.save(stock);

        // Record movement
        recordMovement(product, null, location, request.getQuantity(),
                MovementType.STOCK_IN, request.getReason(), performedBy);

        log.info("Stock added: {} x{} at {} (batch: {})",
                product.getName(), request.getQuantity(), location.getName(), request.getBatchNumber());

        return mapper.toStockResponse(stock);
    }

    @Override
    @Transactional
    @CacheEvict(value = "products", key = "#request.productId")
    public StockResponse removeStock(StockRequest request, String performedBy) {
        Product product = findProduct(request.getProductId());
        Location location = findLocation(request.getLocationId());

        // FEFO: get available batches ordered by expiry (earliest first)
        List<Stock> availableBatches = stockRepository.findAvailableStockByProductId(request.getProductId());
        int totalAvailable = availableBatches.stream().mapToInt(Stock::getQuantity).sum();

        if (totalAvailable < request.getQuantity()) {
            throw new InsufficientStockException(product.getName(), request.getQuantity(), totalAvailable);
        }

        // Deduct using FEFO
        int remaining = request.getQuantity();
        Stock lastModified = null;
        for (Stock batch : availableBatches) {
            if (remaining <= 0) break;

            int deduction = Math.min(batch.getQuantity(), remaining);
            batch.setQuantity(batch.getQuantity() - deduction);
            remaining -= deduction;
            lastModified = stockRepository.save(batch);
        }

        // Record movement
        recordMovement(product, location, null, request.getQuantity(),
                MovementType.STOCK_OUT, request.getReason(), performedBy);

        // Check low stock → Kafka alert
        int newTotal = stockRepository.getTotalStockByProductId(product.getId());
        if (newTotal <= product.getMinStockLevel()) {
            eventPublisher.publishLowStockAlert(
                    product.getId(), product.getName(), product.getSku(),
                    newTotal, product.getMinStockLevel(), location.getName());
        }

        log.info("Stock removed: {} x{} from {}", product.getName(), request.getQuantity(), location.getName());
        return mapper.toStockResponse(lastModified);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockResponse> getStockByProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product", "id", productId);
        }
        return stockRepository.findByProductId(productId).stream()
                .map(mapper::toStockResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockResponse> getStockByLocation(Long locationId) {
        if (!locationRepository.existsById(locationId)) {
            throw new ResourceNotFoundException("Location", "id", locationId);
        }
        return stockRepository.findByLocationId(locationId).stream()
                .map(mapper::toStockResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockResponse> getLowStockItems() {
        return stockRepository.findLowStockItems().stream()
                .map(mapper::toStockResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockResponse> getExpiringItems(int daysAhead) {
        LocalDate cutoff = LocalDate.now().plusDays(daysAhead);
        return stockRepository.findExpiringBefore(cutoff).stream()
                .map(mapper::toStockResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public int getTotalStock(Long productId) {
        return stockRepository.getTotalStockByProductId(productId);
    }

    // ── Helpers ─────────────────────────────────────────────────

    private Product findProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
    }

    private Location findLocation(Long id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location", "id", id));
    }

    private void recordMovement(Product product, Location from, Location to,
                                 int quantity, MovementType type, String reason, String performedBy) {
        StockMovement movement = StockMovement.builder()
                .product(product)
                .fromLocation(from)
                .toLocation(to)
                .quantity(quantity)
                .movementType(type)
                .reason(reason)
                .performedBy(performedBy)
                .build();
        movementRepository.save(movement);
    }
}
