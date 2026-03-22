package com.medicalinventory.order.service;

import com.medicalinventory.common.dto.PagedResponse;
import com.medicalinventory.order.domain.enums.OrderStatus;
import com.medicalinventory.order.dto.OrderRequest;
import com.medicalinventory.order.dto.OrderResponse;
import com.medicalinventory.order.dto.OrderStatusUpdateRequest;

public interface OrderService {

    OrderResponse createOrder(OrderRequest request, String username);

    OrderResponse getOrderById(Long id);

    OrderResponse getOrderByOrderNumber(String orderNumber);

    PagedResponse<OrderResponse> getOrdersByUser(String username, int page, int size);

    PagedResponse<OrderResponse> getOrdersByStatus(OrderStatus status, int page, int size);

    PagedResponse<OrderResponse> getAllOrders(int page, int size);

    OrderResponse updateOrderStatus(Long id, OrderStatusUpdateRequest request, String approvedBy);

    void deleteOrder(Long id);
}
