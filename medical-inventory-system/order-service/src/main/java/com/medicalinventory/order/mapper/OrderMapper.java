package com.medicalinventory.order.mapper;

import com.medicalinventory.order.domain.Order;
import com.medicalinventory.order.domain.OrderItem;
import com.medicalinventory.order.dto.OrderItemResponse;
import com.medicalinventory.order.dto.OrderResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderResponse toOrderResponse(Order order);

    OrderItemResponse toOrderItemResponse(OrderItem orderItem);
}
