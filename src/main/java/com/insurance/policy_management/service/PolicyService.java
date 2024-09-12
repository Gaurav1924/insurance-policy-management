package com.insurance.policy_management.service;

import com.insurance.policy_management.exceptions.ResourceNotFoundException;
import com.insurance.policy_management.model.Customer;
import com.insurance.policy_management.model.Policy;
import com.insurance.policy_management.repository.CustomerRepository;
import com.insurance.policy_management.repository.PolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PolicyService {

    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @CachePut(value = "policy", key = "#policy.id")
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Policy createPolicy(Policy policy) {
        Optional<Customer> customerOptional = customerRepository.findById(policy.getCustomerId());

        if (!customerOptional.isPresent()) {
            throw new ResourceNotFoundException("Customer not found with id: " + policy.getCustomerId());
        }

        Customer customer = customerOptional.get();
        policy.setCustomerId(customer.getId());

        return policyRepository.save(policy);
    }
    @Cacheable(value = "policies")
    public List<Policy> getAllPolicies() {
        return policyRepository.findAll();
    }

    @Cacheable(value = "policy", key = "#id")
    public Optional<Policy> getPolicyById(Long id) {
        return Optional.ofNullable(policyRepository.findById(id))
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found with id: " + id));
    }

    @CachePut(value = "policy", key = "#id")
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Policy updatePolicy(Long id, Policy policyDetails) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found with id: " + id));
        policy.setPolicyNumber(policyDetails.getPolicyNumber());
        policy.setType(policyDetails.getType());
        policy.setCoverageAmount(policyDetails.getCoverageAmount());
        policy.setStartDate(policyDetails.getStartDate());
        policy.setEndDate(policyDetails.getEndDate());
        policy.setPremium(policyDetails.getPremium());
        return policyRepository.save(policy);
    }

    @CacheEvict(value = "policy", key = "#id")
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void deletePolicy(Long id) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found with id: " + id));
        policyRepository.deleteById(id);
    }
}
