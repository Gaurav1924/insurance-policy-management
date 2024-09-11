package com.insurance.policy_management.services;

import com.insurance.policy_management.exceptions.ResourceNotFoundException;
import com.insurance.policy_management.model.Customer;
import com.insurance.policy_management.model.Policy;
import com.insurance.policy_management.repository.CustomerRepository;
import com.insurance.policy_management.repository.PolicyRepository;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class PolicyService {

    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RedissonClient redissonClient;

    @CachePut(value = "policy", key = "#policy.id")
    public Policy createPolicy(Policy policy) {
        RLock lock = redissonClient.getLock("createPolicyLock:" + policy.getCustomer().getId());
        try {
            if (lock.tryLock(10, TimeUnit.SECONDS)) {
                Optional<Customer> customerOptional = customerRepository.findById(policy.getCustomer().getId());

                if (!customerOptional.isPresent()) {
                    throw new ResourceNotFoundException("Customer not found with id: " + policy.getCustomer().getId());
                }

                policy.setCustomer(customerOptional.get());
                return policyRepository.save(policy);
            } else {
                throw new IllegalStateException("Unable to acquire Redis lock to create policy");
            }
        } catch (InterruptedException e) {
            throw new IllegalStateException("Error occurred while acquiring Redis lock", e);
        } finally {
            lock.unlock();
        }
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
    public Policy updatePolicy(Long id, Policy policyDetails) {
        RLock lock = redissonClient.getLock("updatePolicyLock:" + id);
        try {
            if (lock.tryLock(10, TimeUnit.SECONDS)) {
                Policy policy = policyRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Policy not found with id: " + id));
                policy.setPolicyNumber(policyDetails.getPolicyNumber());
                policy.setType(policyDetails.getType());
                policy.setCoverageAmount(policyDetails.getCoverageAmount());
                policy.setStartDate(policyDetails.getStartDate());
                policy.setEndDate(policyDetails.getEndDate());
                policy.setPremium(policyDetails.getPremium());
                return policyRepository.save(policy);
            } else {
                throw new IllegalStateException("Unable to acquire Redis lock to update policy");
            }
        } catch (InterruptedException e) {
            throw new IllegalStateException("Error occurred while acquiring Redis lock", e);
        } finally {
            lock.unlock();
        }
    }

    @CacheEvict(value = "policy", key = "#id")
    public void deletePolicy(Long id) {
        RLock lock = redissonClient.getLock("deletePolicyLock:" + id);
        try {
            if (lock.tryLock(10, TimeUnit.SECONDS)) {
                Policy policy = policyRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Policy not found with id: " + id));
                policyRepository.deleteById(id);
            } else {
                throw new IllegalStateException("Unable to acquire Redis lock to delete policy");
            }
        } catch (InterruptedException e) {
            throw new IllegalStateException("Error occurred while acquiring Redis lock", e);
        } finally {
            lock.unlock();
        }
    }
}
