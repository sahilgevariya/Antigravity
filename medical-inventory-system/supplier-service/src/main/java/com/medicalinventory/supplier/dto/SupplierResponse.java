package com.medicalinventory.supplier.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierResponse {

    private Long id;
    private String name;
    private String contactName;
    private String email;
    private String phone;
    private String address;
    private Double rating;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
