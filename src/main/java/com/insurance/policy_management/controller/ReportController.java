package com.insurance.policy_management.controller;

import com.insurance.policy_management.dto.ApiResponse;
import com.insurance.policy_management.model.Policy;
import com.insurance.policy_management.service.ReportService;
import jakarta.validation.constraints.PastOrPresent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@Validated // Enables validation for request parameters
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/active-policies")
    public ResponseEntity<ApiResponse> getActivePoliciesReport() {
        List<Policy> activePolicies = reportService.generateActivePoliciesReport();
        return ResponseEntity.ok(new ApiResponse(true, "Active policies retrieved successfully", activePolicies));
    }

    @GetMapping("/expired-policies")
    public ResponseEntity<ApiResponse> getExpiredPoliciesReport() {
        List<Policy> expiredPolicies = reportService.generateExpiredPoliciesReport();
        return ResponseEntity.ok(new ApiResponse(true, "Expired policies retrieved successfully", expiredPolicies));
    }

    @GetMapping("/summary-by-customer")
    public ResponseEntity<ApiResponse> getPoliciesSummaryByCustomer(@RequestParam(value = "customerId", required = false) Long customerId) {
        Map<String, List<Policy>> summary;

        // Check if customerId is passed; fetch summary accordingly
        if (customerId != null) {
            summary = reportService.generatePoliciesSummaryByCustomer(customerId);
        } else {
            summary = reportService.generatePoliciesSummaryForAllCustomers();
        }

        return ResponseEntity.ok(new ApiResponse(true, "Policies summary retrieved successfully", summary));
    }

    @GetMapping("/by-type")
    public ResponseEntity<ApiResponse> getPoliciesByTypeReport(
            @RequestParam @NotBlank(message = "Type is required") String type) {
        Map<String, List<Policy>> policiesByType = reportService.generatePoliciesByTypeReport(type);
        return ResponseEntity.ok(new ApiResponse(true, "Policies by type retrieved successfully", policiesByType));
    }

    @GetMapping("/by-date-range")
    public ResponseEntity<ApiResponse> getPoliciesByDateRange(
            @RequestParam @NotNull(message = "Start date is required") @PastOrPresent(message = "Start date cannot be in the future") Date startDate,
            @RequestParam @NotNull(message = "End date is required") Date endDate,
            BindingResult result) {

        if (result.hasErrors()) {
            String errorMessage = result.getFieldError().getDefaultMessage();
            return ResponseEntity.badRequest().body(new ApiResponse(false, errorMessage));
        }

        if (endDate.before(startDate)) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "End date must be after start date"));
        }

        List<Policy> policiesByDateRange = reportService.getPoliciesByDateRange(startDate, endDate);
        return ResponseEntity.ok(new ApiResponse(true, "Policies by date range retrieved successfully", policiesByDateRange));
    }
}
