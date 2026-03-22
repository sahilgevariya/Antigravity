package com.medicalinventory.supplier.service;

import com.medicalinventory.common.dto.PagedResponse;
import com.medicalinventory.supplier.dto.SupplierRequest;
import com.medicalinventory.supplier.dto.SupplierResponse;

public interface SupplierService {

    SupplierResponse createSupplier(SupplierRequest request);

    SupplierResponse getSupplierById(Long id);

    SupplierResponse updateSupplier(Long id, SupplierRequest request);

    PagedResponse<SupplierResponse> getAllSuppliers(int page, int size, String search);

    PagedResponse<SupplierResponse> getActiveSuppliers(int page, int size);
}
