package com.medicalinventory.supplier.repository;

import com.medicalinventory.supplier.domain.PurchaseOrder;
import com.medicalinventory.supplier.domain.enums.PurchaseOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

    Optional<PurchaseOrder> findByPoNumber(String poNumber);

    Page<PurchaseOrder> findBySupplierId(Long supplierId, Pageable pageable);

    Page<PurchaseOrder> findByStatus(PurchaseOrderStatus status, Pageable pageable);
}
