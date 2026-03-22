package com.medicalinventory.inventory.service;

import com.medicalinventory.inventory.dto.StockRequest;
import com.medicalinventory.inventory.dto.StockResponse;

import java.util.List;

public interface StockService {

    StockResponse addStock(StockRequest request, String performedBy);

    StockResponse removeStock(StockRequest request, String performedBy);

    List<StockResponse> getStockByProduct(Long productId);

    List<StockResponse> getStockByLocation(Long locationId);

    List<StockResponse> getLowStockItems();

    List<StockResponse> getExpiringItems(int daysAhead);

    int getTotalStock(Long productId);
}
