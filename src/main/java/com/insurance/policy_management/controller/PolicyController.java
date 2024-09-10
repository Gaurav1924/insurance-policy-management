package com.insurance.policy_management.controller;

import com.insurance.policy_management.dto.ApiResponse;
import com.insurance.policy_management.model.Policy;
import com.insurance.policy_management.services.PolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/policies")
public class PolicyController {

    @Autowired
    private PolicyService policyService;

    @PostMapping
    public ResponseEntity<ApiResponse> createPolicy(@Valid @RequestBody Policy policy, BindingResult result) {
        if (result.hasErrors()) {
            String errorMessage = result.getFieldError().getDefaultMessage();
            return ResponseEntity.badRequest().body(new ApiResponse(false, errorMessage));
        }

        Policy createdPolicy = policyService.createPolicy(policy);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "Policy created successfully", createdPolicy));
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllPolicies() {
        List<Policy> policies = policyService.getAllPolicies();
        if (policies.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse(true, "No policies found"));
        }
        return ResponseEntity.ok(new ApiResponse(true, "Policies retrieved successfully", policies));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getPolicyById(@PathVariable Long id) {
        Optional<Policy> policy = policyService.getPolicyById(id);
        if (policy.isPresent()) {
            return ResponseEntity.ok(new ApiResponse(true, "Policy retrieved successfully", policy.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "Policy not found"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updatePolicy(@PathVariable Long id, @Valid @RequestBody Policy policyDetails, BindingResult result) {
        if (result.hasErrors()) {
            String errorMessage = result.getFieldError().getDefaultMessage();
            return ResponseEntity.badRequest().body(new ApiResponse(false, errorMessage));
        }

        Optional<Policy> existingPolicy = policyService.getPolicyById(id);
        if (existingPolicy.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "Policy not found"));
        }

        Policy updatedPolicy = policyService.updatePolicy(id, policyDetails);
        return ResponseEntity.ok(new ApiResponse(true, "Policy updated successfully", updatedPolicy));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deletePolicy(@PathVariable Long id) {
        Optional<Policy> policy = policyService.getPolicyById(id);
        if (policy.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "Policy not found"));
        }

        policyService.deletePolicy(id);
        return ResponseEntity.ok(new ApiResponse(true, "Policy deleted successfully"));
    }
}
