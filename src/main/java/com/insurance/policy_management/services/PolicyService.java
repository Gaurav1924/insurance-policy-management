package com.insurance.policy_management.services;

import com.insurance.policy_management.model.Customer;
import com.insurance.policy_management.model.Policy;
import com.insurance.policy_management.repository.CustomerRepository;
import com.insurance.policy_management.repository.PolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PolicyService {

    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private CustomerRepository customerRepository;

    public Policy createPolicy(Policy policy) {
        Optional<Customer> customerOptional = customerRepository.findById(policy.getCustomer().getId());

        if (!customerOptional.isPresent()) {
            throw new RuntimeException("Customer not found with id: " + policy.getCustomer().getId());
        }

        policy.setCustomer(customerOptional.get());
        return policyRepository.save(policy);
    }

    public List<Policy> getAllPolicies() {
        return policyRepository.findAll();
    }

    public Optional<Policy> getPolicyById(Long id) {
        return policyRepository.findById(id);
    }

    public Policy updatePolicy(Long id, Policy policyDetails) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Policy not found"));
        policy.setPolicyNumber(policyDetails.getPolicyNumber());
        policy.setType(policyDetails.getType());
        policy.setCoverageAmount(policyDetails.getCoverageAmount());
        policy.setStartDate(policyDetails.getStartDate());
        policy.setEndDate(policyDetails.getEndDate());
        policy.setPremium(policyDetails.getPremium());
        return policyRepository.save(policy);
    }

    public void deletePolicy(Long id) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Policy not found"));
        policyRepository.delete(policy);
    }
}
