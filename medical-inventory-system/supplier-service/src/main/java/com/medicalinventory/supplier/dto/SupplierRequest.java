package com.medicalinventory.supplier.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierRequest {

    @NotBlank(message = "Supplier name is required")
    @Size(max = 255)
    private String name;

    @Size(max = 255)
    private String contactName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 255)
    private String email;

    @Size(max = 50)
    private String phone;

    @Size(max = 500)
    private String address;

    private Double rating;

    private boolean active = true;
}
