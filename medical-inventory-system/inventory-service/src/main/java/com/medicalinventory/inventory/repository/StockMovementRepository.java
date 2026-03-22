package com.medicalinventory.inventory.repository;

import com.medicalinventory.inventory.domain.StockMovement;
import com.medicalinventory.inventory.domain.enums.MovementType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    Page<StockMovement> findByProductId(Long productId, Pageable pageable);

    Page<StockMovement> findByMovementType(MovementType movementType, Pageable pageable);
}
