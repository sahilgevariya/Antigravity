package com.medicalinventory.supplier.mapper;

import com.medicalinventory.supplier.domain.PurchaseOrder;
import com.medicalinventory.supplier.domain.PurchaseOrderItem;
import com.medicalinventory.supplier.domain.Supplier;
import com.medicalinventory.supplier.dto.PurchaseOrderItemResponse;
import com.medicalinventory.supplier.dto.PurchaseOrderResponse;
import com.medicalinventory.supplier.dto.SupplierRequest;
import com.medicalinventory.supplier.dto.SupplierResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface SupplierMapper {

    Supplier toSupplier(SupplierRequest request);

    SupplierResponse toSupplierResponse(Supplier supplier);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateSupplierFromRequest(SupplierRequest request, @MappingTarget Supplier supplier);

    PurchaseOrderResponse toPurchaseOrderResponse(PurchaseOrder purchaseOrder);

    PurchaseOrderItemResponse toPurchaseOrderItemResponse(PurchaseOrderItem purchaseOrderItem);
}
