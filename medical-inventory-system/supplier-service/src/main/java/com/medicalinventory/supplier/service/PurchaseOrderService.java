package com.medicalinventory.supplier.service;

import com.medicalinventory.common.dto.PagedResponse;
import com.medicalinventory.supplier.domain.enums.PurchaseOrderStatus;
import com.medicalinventory.supplier.dto.PurchaseOrderRequest;
import com.medicalinventory.supplier.dto.PurchaseOrderResponse;

public interface PurchaseOrderService {

    PurchaseOrderResponse createPurchaseOrder(PurchaseOrderRequest request, String username);

    PurchaseOrderResponse getPurchaseOrderById(Long id);

    PurchaseOrderResponse getPurchaseOrderByPoNumber(String poNumber);

    PagedResponse<PurchaseOrderResponse> getPurchaseOrdersBySupplier(Long supplierId, int page, int size);

    PagedResponse<PurchaseOrderResponse> getPurchaseOrdersByStatus(PurchaseOrderStatus status, int page, int size);

    PagedResponse<PurchaseOrderResponse> getAllPurchaseOrders(int page, int size);

    PurchaseOrderResponse updatePurchaseOrderStatus(Long id, PurchaseOrderStatus status);
}
