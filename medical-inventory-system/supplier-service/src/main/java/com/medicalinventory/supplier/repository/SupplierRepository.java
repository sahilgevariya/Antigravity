package com.medicalinventory.supplier.repository;

import com.medicalinventory.supplier.domain.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    Optional<Supplier> findByEmail(String email);

    Page<Supplier> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Supplier> findByActiveTrue(Pageable pageable);
}
