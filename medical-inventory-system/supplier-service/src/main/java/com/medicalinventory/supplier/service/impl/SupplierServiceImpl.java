package com.medicalinventory.supplier.service.impl;

import com.medicalinventory.common.dto.PagedResponse;
import com.medicalinventory.common.exception.DuplicateResourceException;
import com.medicalinventory.common.exception.ResourceNotFoundException;
import com.medicalinventory.supplier.domain.Supplier;
import com.medicalinventory.supplier.dto.SupplierRequest;
import com.medicalinventory.supplier.dto.SupplierResponse;
import com.medicalinventory.supplier.mapper.SupplierMapper;
import com.medicalinventory.supplier.repository.SupplierRepository;
import com.medicalinventory.supplier.service.SupplierService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper mapper;

    @Override
    @Transactional
    public SupplierResponse createSupplier(SupplierRequest request) {
        if (supplierRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Supplier", "email", request.getEmail());
        }

        Supplier supplier = mapper.toSupplier(request);
        supplier = supplierRepository.save(supplier);
        log.info("Created supplier: {}", supplier.getName());
        return mapper.toSupplierResponse(supplier);
    }

    @Override
    @Transactional(readOnly = true)
    public SupplierResponse getSupplierById(Long id) {
        return supplierRepository.findById(id)
                .map(mapper::toSupplierResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", id));
    }

    @Override
    @Transactional
    public SupplierResponse updateSupplier(Long id, SupplierRequest request) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", id));

        if (!supplier.getEmail().equals(request.getEmail()) &&
                supplierRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Supplier", "email", request.getEmail());
        }

        mapper.updateSupplierFromRequest(request, supplier);
        supplier = supplierRepository.save(supplier);
        log.info("Updated supplier: {}", supplier.getName());
        return mapper.toSupplierResponse(supplier);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<SupplierResponse> getAllSuppliers(int page, int size, String search) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Supplier> supplierPage;

        if (search != null && !search.trim().isEmpty()) {
            supplierPage = supplierRepository.findByNameContainingIgnoreCase(search.trim(), pageRequest);
        } else {
            supplierPage = supplierRepository.findAll(pageRequest);
        }

        return toPagedResponse(supplierPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<SupplierResponse> getActiveSuppliers(int page, int size) {
        Page<Supplier> supplierPage = supplierRepository.findByActiveTrue(
                PageRequest.of(page, size, Sort.by("name").ascending()));
        return toPagedResponse(supplierPage);
    }

    private PagedResponse<SupplierResponse> toPagedResponse(Page<Supplier> page) {
        List<SupplierResponse> content = page.getContent().stream()
                .map(mapper::toSupplierResponse)
                .toList();
        return PagedResponse.<SupplierResponse>builder()
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
