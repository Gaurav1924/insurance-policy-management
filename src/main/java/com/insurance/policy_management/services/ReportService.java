package com.insurance.policy_management.services;

import com.insurance.policy_management.exceptions.ResourceNotFoundException;
import com.insurance.policy_management.model.Policy;
import com.insurance.policy_management.repository.PolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private PolicyRepository policyRepository;

    public List<Policy> generateActivePoliciesReport() {
        Date today = new Date();
        return policyRepository.findAll().stream()
                .filter(policy -> policy.getStartDate().before(today) && policy.getEndDate().after(today))
                .collect(Collectors.toList());
    }

    public List<Policy> generateExpiredPoliciesReport() {
        Date today = new Date();
        return policyRepository.findAll().stream()
                .filter(policy -> policy.getEndDate().before(today))
                .collect(Collectors.toList());
    }

    public Map<String, List<Policy>> generatePoliciesSummaryByCustomer() {
        return policyRepository.findAll().stream()
                .collect(Collectors.groupingBy(policy -> policy.getCustomer().getName()));
    }

    public Map<String, List<Policy>> generatePoliciesByTypeReport(String type) {
        return policyRepository.findAll().stream()
                .filter(policy -> policy.getType().equalsIgnoreCase(type))
                .collect(Collectors.groupingBy(Policy::getType));
    }

    public List<Policy> getPoliciesByDateRange(Date startDate, Date endDate) {
        List<Policy> policies = policyRepository.findByDateRange(startDate, endDate);
        if (policies.isEmpty()) {
            throw new ResourceNotFoundException("No policies found for the given date range");
        }
        return policies;
    }
}
