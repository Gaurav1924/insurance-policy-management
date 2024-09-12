package com.insurance.policy_management.service;

import com.insurance.policy_management.exceptions.ResourceNotFoundException;
import com.insurance.policy_management.model.Customer;
import com.insurance.policy_management.model.Policy;
import com.insurance.policy_management.repository.CustomerRepository;
import com.insurance.policy_management.repository.PolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private PolicyRepository policyRepository;

    @Cacheable(value = "activePolicies")
    public List<Policy> generateActivePoliciesReport() {
        Date today = new Date();
        return policyRepository.findAll().stream()
                .filter(policy -> policy.getStartDate().before(today) && policy.getEndDate().after(today))
                .collect(Collectors.toList());
    }

    @Cacheable(value = "expiredPolicies")
    public List<Policy> generateExpiredPoliciesReport() {
        Date today = new Date();
        return policyRepository.findAll().stream()
                .filter(policy -> policy.getEndDate().before(today))
                .collect(Collectors.toList());
    }

    @Cacheable(value = "summaryByCustomer", key = "#customerId")
    public Map<String, List<Policy>> generatePoliciesSummaryByCustomer(Long customerId) {
        return policyRepository.findAll().stream()
                .filter(policy -> policy.getCustomerId().equals(customerId))
                .collect(Collectors.groupingBy(policy -> policy.getCustomerId().toString()));
    }

    // Get policies summary for all customers
    @Cacheable(value = "summaryByCustomer")
    public Map<String, List<Policy>> generatePoliciesSummaryForAllCustomers() {
        return policyRepository.findAll().stream()
                .collect(Collectors.groupingBy(policy -> policy.getCustomerId().toString()));
    }

    @Cacheable(value = "policiesByType", key = "#type")
    public Map<String, List<Policy>> generatePoliciesByTypeReport(String type) {
        return policyRepository.findAll().stream()
                .filter(policy -> policy.getType().equalsIgnoreCase(type))
                .collect(Collectors.groupingBy(Policy::getType));
    }

    @Cacheable(value = "policiesByDateRange", key = "{#startDate, #endDate}")
    public List<Policy> getPoliciesByDateRange(Date startDate, Date endDate) {
        List<Policy> policies = policyRepository.findByDateRange(startDate, endDate);
        if (policies.isEmpty()) {
            throw new ResourceNotFoundException("No policies found for the given date range");
        }
        return policies;
    }
}
