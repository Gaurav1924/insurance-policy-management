package com.insurance.policy_management.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CustomerRequestDto {
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Contact information is required")
    @Column(unique = true)
    private String contactInformation;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Identification details are required")
    private String identificationDetails;
}
