package com.medicalinventory.order.service.impl;

import com.medicalinventory.common.dto.ApiResponse;
import com.medicalinventory.common.dto.PagedResponse;
import com.medicalinventory.common.exception.BusinessRuleException;
import com.medicalinventory.common.exception.ResourceNotFoundException;
import com.medicalinventory.order.client.InventoryClient;
import com.medicalinventory.order.domain.Order;
import com.medicalinventory.order.domain.OrderItem;
import com.medicalinventory.order.domain.enums.OrderStatus;
import com.medicalinventory.order.dto.OrderItemRequest;
import com.medicalinventory.order.dto.OrderRequest;
import com.medicalinventory.order.dto.OrderResponse;
import com.medicalinventory.order.dto.OrderStatusUpdateRequest;
import com.medicalinventory.order.mapper.OrderMapper;
import com.medicalinventory.order.repository.OrderRepository;
import com.medicalinventory.order.service.OrderService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
    private final OrderMapper mapper;

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest request, String username) {
        Order order = Order.builder()
                .orderNumber(generateOrderNumber())
                .requestingUser(username)
                .requestingLocation(request.getRequestingLocation())
                .remarks(request.getRemarks())
                .status(OrderStatus.PENDING)
                .build();

        for (OrderItemRequest itemReq : request.getItems()) {
            InventoryClient.ProductDto product = fetchProductFromInventory(itemReq.getProductId());

            OrderItem orderItem = OrderItem.builder()
                    .productId(product.getId())
                    .productSku(product.getSku())
                    .productName(product.getName())
                    .quantityRequested(itemReq.getQuantity())
                    .notes(itemReq.getNotes())
                    .build();

            order.addItem(orderItem);
        }

        order = orderRepository.save(order);
        log.info("Order created: {} by {}", order.getOrderNumber(), username);
        return mapper.toOrderResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(mapper::toOrderResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .map(mapper::toOrderResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "orderNumber", orderNumber));
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<OrderResponse> getOrdersByUser(String username, int page, int size) {
        Page<Order> orderPage = orderRepository.findByRequestingUser(username,
                PageRequest.of(page, size, Sort.by("createdAt").descending()));
        return toPagedResponse(orderPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<OrderResponse> getOrdersByStatus(OrderStatus status, int page, int size) {
        Page<Order> orderPage = orderRepository.findByStatus(status,
                PageRequest.of(page, size, Sort.by("createdAt").descending()));
        return toPagedResponse(orderPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<OrderResponse> getAllOrders(int page, int size) {
        Page<Order> orderPage = orderRepository.findAll(
                PageRequest.of(page, size, Sort.by("createdAt").descending()));
        return toPagedResponse(orderPage);
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long id, OrderStatusUpdateRequest request, String approvedBy) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));

        // State machine rules (basic)
        if (order.getStatus() == OrderStatus.FULFILLED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new BusinessRuleException("Cannot update status of a " + order.getStatus() + " order");
        }

        order.setStatus(request.getStatus());

        if (request.getRemarks() != null) {
            order.setRemarks(order.getRemarks() + " | " + request.getRemarks());
        }

        if (request.getStatus() == OrderStatus.APPROVED || request.getStatus() == OrderStatus.REJECTED) {
            order.setApprovedBy(approvedBy);
            order.setApprovedAt(LocalDateTime.now());
        }

        order = orderRepository.save(order);
        log.info("Order {} status updated to {} by {}", order.getOrderNumber(), request.getStatus(), approvedBy);

        // TODO: In Phase 2, if status is APPROVED/FULFILLED, trigger Inventory deduction via Kafka/gRPC

        return mapper.toOrderResponse(order);
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessRuleException("Only PENDING orders can be deleted. Cancel it instead.");
        }

        orderRepository.delete(order);
        log.info("Order deleted: {}", order.getOrderNumber());
    }

    // ── Helpers ──────────────────────────────────────────────────

    private InventoryClient.ProductDto fetchProductFromInventory(Long productId) {
        try {
            ApiResponse<InventoryClient.ProductDto> response = inventoryClient.getProductById(productId);
            if (response.getData() == null || !response.getData().isActive()) {
                throw new BusinessRuleException("Product ID " + productId + " is inactive or unavailable");
            }
            return response.getData();
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Product in Inventory", "id", productId);
        } catch (FeignException e) {
            log.error("Error communicating with Inventory Service: {}", e.getMessage());
            throw new BusinessRuleException("Unable to verify product details at this time");
        }
    }

    private String generateOrderNumber() {
        return "ORD-" + LocalDateTime.now().getYear() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private PagedResponse<OrderResponse> toPagedResponse(Page<Order> orderPage) {
        List<OrderResponse> content = orderPage.getContent().stream()
                .map(mapper::toOrderResponse)
                .toList();
        return PagedResponse.<OrderResponse>builder()
                .content(content)
                .page(orderPage.getNumber())
                .size(orderPage.getSize())
                .totalElements(orderPage.getTotalElements())
                .totalPages(orderPage.getTotalPages())
                .first(orderPage.isFirst())
                .last(orderPage.isLast())
                .build();
    }
}
