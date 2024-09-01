package com.insurance.policy_management.controller;

import com.insurance.policy_management.model.Policy;
import com.insurance.policy_management.services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/active-policies")
    public List<Policy> getActivePoliciesReport() {
        return reportService.generateActivePoliciesReport();
    }

    @GetMapping("/expired-policies")
    public List<Policy> getExpiredPoliciesReport() {
        return reportService.generateExpiredPoliciesReport();
    }

    @GetMapping("/summary-by-customer")
    public Map<String, List<Policy>> getPoliciesSummaryByCustomer() {
        return reportService.generatePoliciesSummaryByCustomer();
    }

    @GetMapping("/by-type")
    public Map<String, List<Policy>> getPoliciesByTypeReport(@RequestParam String type) {
        return reportService.generatePoliciesByTypeReport(type);
    }
}
