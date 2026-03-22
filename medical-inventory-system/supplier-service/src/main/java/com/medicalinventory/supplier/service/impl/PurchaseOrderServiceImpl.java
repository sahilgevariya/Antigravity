package com.medicalinventory.supplier.service.impl;

import com.medicalinventory.common.dto.ApiResponse;
import com.medicalinventory.common.dto.PagedResponse;
import com.medicalinventory.common.exception.BusinessRuleException;
import com.medicalinventory.common.exception.ResourceNotFoundException;
import com.medicalinventory.supplier.client.InventoryClient;
import com.medicalinventory.supplier.domain.PurchaseOrder;
import com.medicalinventory.supplier.domain.PurchaseOrderItem;
import com.medicalinventory.supplier.domain.Supplier;
import com.medicalinventory.supplier.domain.enums.PurchaseOrderStatus;
import com.medicalinventory.supplier.dto.PurchaseOrderItemRequest;
import com.medicalinventory.supplier.dto.PurchaseOrderRequest;
import com.medicalinventory.supplier.dto.PurchaseOrderResponse;
import com.medicalinventory.supplier.mapper.SupplierMapper;
import com.medicalinventory.supplier.repository.PurchaseOrderRepository;
import com.medicalinventory.supplier.repository.SupplierRepository;
import com.medicalinventory.supplier.service.PurchaseOrderService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final SupplierRepository supplierRepository;
    private final InventoryClient inventoryClient;
    private final SupplierMapper mapper;

    @Override
    @Transactional
    public PurchaseOrderResponse createPurchaseOrder(PurchaseOrderRequest request, String username) {
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", request.getSupplierId()));

        if (!supplier.isActive()) {
            throw new BusinessRuleException("Cannot create PO for an inactive supplier");
        }

        PurchaseOrder po = PurchaseOrder.builder()
                .poNumber(generatePoNumber())
                .supplier(supplier)
                .expectedDate(request.getExpectedDate())
                .notes(request.getNotes())
                .createdBy(username)
                .status(PurchaseOrderStatus.DRAFT)
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (PurchaseOrderItemRequest itemReq : request.getItems()) {
            InventoryClient.ProductDto product = fetchProductFromInventory(itemReq.getProductId());

            PurchaseOrderItem poItem = PurchaseOrderItem.builder()
                    .productId(product.getId())
                    .productSku(product.getSku())
                    .productName(product.getName())
                    .quantity(itemReq.getQuantity())
                    .unitPrice(itemReq.getUnitPrice())
                    .build();

            totalAmount = totalAmount.add(
                    itemReq.getUnitPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()))
            );

            po.addItem(poItem);
        }

        po.setTotalAmount(totalAmount);
        po = purchaseOrderRepository.save(po);

        log.info("Created Purchase Order: {} by {}", po.getPoNumber(), username);
        return mapper.toPurchaseOrderResponse(po);
    }

    @Override
    @Transactional(readOnly = true)
    public PurchaseOrderResponse getPurchaseOrderById(Long id) {
        return purchaseOrderRepository.findById(id)
                .map(mapper::toPurchaseOrderResponse)
                .orElseThrow(() -> new ResourceNotFoundException("PurchaseOrder", "id", id));
    }

    @Override
    @Transactional(readOnly = true)
    public PurchaseOrderResponse getPurchaseOrderByPoNumber(String poNumber) {
        return purchaseOrderRepository.findByPoNumber(poNumber)
                .map(mapper::toPurchaseOrderResponse)
                .orElseThrow(() -> new ResourceNotFoundException("PurchaseOrder", "poNumber", poNumber));
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<PurchaseOrderResponse> getPurchaseOrdersBySupplier(Long supplierId, int page, int size) {
        Page<PurchaseOrder> poPage = purchaseOrderRepository.findBySupplierId(supplierId,
                PageRequest.of(page, size, Sort.by("createdAt").descending()));
        return toPagedResponse(poPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<PurchaseOrderResponse> getPurchaseOrdersByStatus(PurchaseOrderStatus status, int page, int size) {
        Page<PurchaseOrder> poPage = purchaseOrderRepository.findByStatus(status,
                PageRequest.of(page, size, Sort.by("createdAt").descending()));
        return toPagedResponse(poPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<PurchaseOrderResponse> getAllPurchaseOrders(int page, int size) {
        Page<PurchaseOrder> poPage = purchaseOrderRepository.findAll(
                PageRequest.of(page, size, Sort.by("createdAt").descending()));
        return toPagedResponse(poPage);
    }

    @Override
    @Transactional
    public PurchaseOrderResponse updatePurchaseOrderStatus(Long id, PurchaseOrderStatus status) {
        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PurchaseOrder", "id", id));

        // State Machine
        if (po.getStatus() == PurchaseOrderStatus.DELIVERED || po.getStatus() == PurchaseOrderStatus.CANCELLED) {
            throw new BusinessRuleException("Cannot change status of a " + po.getStatus() + " order");
        }

        po.setStatus(status);
        po = purchaseOrderRepository.save(po);

        log.info("Purchase Order {} status updated to {}", po.getPoNumber(), status);

        // TODO: In Phase 2, if status becomes DELIVERED, trigger StockMovement to auto-increment inventory

        return mapper.toPurchaseOrderResponse(po);
    }

    // ── Helpers ──────────────────────────────────────────────────

    private InventoryClient.ProductDto fetchProductFromInventory(Long productId) {
        try {
            ApiResponse<InventoryClient.ProductDto> response = inventoryClient.getProductById(productId);
            if (response.getData() == null) {
                throw new BusinessRuleException("Product ID " + productId + " is unavailable");
            }
            return response.getData();
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Product in Inventory", "id", productId);
        } catch (FeignException e) {
            log.error("Error communicating with Inventory Service: {}", e.getMessage());
            throw new BusinessRuleException("Unable to verify product details at this time");
        }
    }

    private String generatePoNumber() {
        return "PO-" + LocalDateTime.now().getYear() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private PagedResponse<PurchaseOrderResponse> toPagedResponse(Page<PurchaseOrder> page) {
        List<PurchaseOrderResponse> content = page.getContent().stream()
                .map(mapper::toPurchaseOrderResponse)
                .toList();
        return PagedResponse.<PurchaseOrderResponse>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }
}
