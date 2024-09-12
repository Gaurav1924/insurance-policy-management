package com.insurance.policy_management.service;

import com.insurance.policy_management.exceptions.ResourceNotFoundException;
import com.insurance.policy_management.model.Claim;
import com.insurance.policy_management.model.Policy;
import com.insurance.policy_management.repository.ClaimRepository;
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
public class ClaimService {

    @Autowired
    private ClaimRepository claimRepository;

    @Autowired
    private PolicyRepository policyRepository;

    @CachePut(value = "claims", key = "#claim.id")
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Claim fileClaim(Claim claim) {
        // Fetch the policy using policy_id from the claim
        Optional<Policy> policyOptional = policyRepository.findById(claim.getPolicyId());

        if (!policyOptional.isPresent()) {
            throw new ResourceNotFoundException("Policy not found with id: " + claim.getPolicyId());
        }

        // Fetch the associated policy to ensure it's valid and has a customer
        Policy policy = policyOptional.get();
        if (policy.getCustomerId() == null) {
            throw new IllegalStateException("Customer is not set for this policy. 'customer_id' cannot be null.");
        }

        // The policy_id is already stored in claim, so no need to set policy again
        return claimRepository.save(claim);
    }

    @Cacheable(value = "claims")
    public List<Claim> getAllClaims() {
        return claimRepository.findAll();
    }

    @Cacheable(value = "claim", key = "#id")
    public Optional<Claim> getClaimById(Long id) {
        return Optional.ofNullable(claimRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found with id: " + id)));
    }

    @CachePut(value = "claim", key = "#id")
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Claim updateClaim(Long id, Claim claimDetails) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found with id: " + id));
        claim.setClaimAmount(claimDetails.getClaimAmount());
        claim.setDateOfIncident(claimDetails.getDateOfIncident());
        claim.setDescription(claimDetails.getDescription());
        claim.setStatus(claimDetails.getStatus());
        return claimRepository.save(claim);
    }

    @CacheEvict(value = "claim", key = "#id")
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void deleteClaim(Long id) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found with id: " + id));
        claimRepository.delete(claim);
    }

    @CacheEvict(value = "claims", allEntries = true)
    public void evictAllClaimsCache() {
        // This method will clear all cached claims
    }
}