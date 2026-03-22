package com.medicalinventory.inventory.repository;

import com.medicalinventory.inventory.domain.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    Optional<Location> findByName(String name);

    boolean existsByName(String name);

    List<Location> findByActiveTrue();

    List<Location> findByTypeAndActiveTrue(String type);
}
