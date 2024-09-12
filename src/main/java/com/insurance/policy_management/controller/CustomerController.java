package com.insurance.policy_management.controller;

import com.insurance.policy_management.dto.ApiResponse;
import com.insurance.policy_management.dto.CustomerRequestDto;
import com.insurance.policy_management.model.Customer;
import com.insurance.policy_management.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PostMapping
    public ResponseEntity<ApiResponse> createCustomer(@Valid @RequestBody CustomerRequestDto RequestDto, BindingResult result) {
        if (result.hasErrors()) {
            String errorMessage = result.getFieldError().getDefaultMessage();
            return ResponseEntity.badRequest().body(new ApiResponse(false, errorMessage));
        }

        Customer createdCustomer = customerService.createCustomer(RequestDto.getName(), RequestDto.getAddress(), RequestDto.getContactInformation(), RequestDto.getIdentificationDetails());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "Customer created successfully", createdCustomer));
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        if (customers.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(new ApiResponse(true, "No customers found"));
        }
        return ResponseEntity.ok(new ApiResponse(true, "Customers retrieved successfully", customers));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getCustomerById(@PathVariable Long id) {
        Optional<Customer> customer = customerService.getCustomerById(id);
        if (customer.isPresent()) {
            return ResponseEntity.ok(new ApiResponse(true, "Customer retrieved successfully", customer.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "Customer not found"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateCustomer(@PathVariable Long id, @Valid @RequestBody Customer customerDetails, BindingResult result) {
        if (result.hasErrors()) {
            String errorMessage = result.getFieldError().getDefaultMessage();
            return ResponseEntity.badRequest().body(new ApiResponse(false, errorMessage));
        }

        Customer updatedCustomer = customerService.updateCustomer(id, customerDetails);
        return ResponseEntity.ok(new ApiResponse(true, "Customer updated successfully", updatedCustomer));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok(new ApiResponse(true, "Customer deleted successfully"));
    }
}
