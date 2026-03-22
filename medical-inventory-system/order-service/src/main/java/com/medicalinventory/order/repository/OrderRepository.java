package com.medicalinventory.order.repository;

import com.medicalinventory.order.domain.Order;
import com.medicalinventory.order.domain.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNumber(String orderNumber);

    Page<Order> findByRequestingUser(String username, Pageable pageable);

    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
}
