package com.insurance.policy_management.services;

import com.insurance.policy_management.exceptions.ResourceNotFoundException;
import com.insurance.policy_management.model.Claim;
import com.insurance.policy_management.model.Policy;
import com.insurance.policy_management.repository.ClaimRepository;
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
public class ClaimService {

    @Autowired
    private ClaimRepository claimRepository;

    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private RedissonClient redissonClient;

    @CachePut(value = "claims", key = "#claim.id")
    public Claim fileClaim(Claim claim) {
        RLock lock = redissonClient.getLock("fileClaimLock:" + claim.getPolicy().getId());
        try {
            if (lock.tryLock(10, TimeUnit.SECONDS)) {
                // Retrieve the Policy associated with the Claim
                Optional<Policy> policyOptional = policyRepository.findById(claim.getPolicy().getId());

                // Throw an exception if the policy is not found
                if (!policyOptional.isPresent()) {
                    throw new ResourceNotFoundException("Policy not found with id: " + claim.getPolicy().getId());
                }

                // Ensure that the policy has a customer associated with it
                Policy policy = policyOptional.get();
                if (policy.getCustomer() == null || policy.getCustomer().getId() == null) {
                    throw new IllegalArgumentException("Customer is not set for this policy. 'customer_id' cannot be null.");
                }

                // Set the policy for the claim and save it
                claim.setPolicy(policy);
                return claimRepository.save(claim);
            } else {
                throw new IllegalStateException("Unable to acquire Redis lock to file claim");
            }
        } catch (InterruptedException e) {
            throw new IllegalStateException("Error occurred while acquiring Redis lock", e);
        } catch (Exception e) {
            throw new IllegalStateException("Error occurred while saving the claim", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock(); // Always release the lock
            }
        }
    }    @Cacheable(value = "claims")
    public List<Claim> getAllClaims() {
        return claimRepository.findAll();
    }

    @Cacheable(value = "claim", key = "#id")
    public Optional<Claim> getClaimById(Long id) {
        return Optional.ofNullable(claimRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found with id: " + id)));
    }

    @CachePut(value = "claim", key = "#id")
    public Claim updateClaim(Long id, Claim claimDetails) {
        RLock lock = redissonClient.getLock("updateClaimLock:" + id);
        try {
            if (lock.tryLock(10, TimeUnit.SECONDS)) {
                Claim claim = claimRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Claim not found with id: " + id));
                claim.setClaimAmount(claimDetails.getClaimAmount());
                claim.setDateOfIncident(claimDetails.getDateOfIncident());
                claim.setDescription(claimDetails.getDescription());
                claim.setStatus(claimDetails.getStatus());
                return claimRepository.save(claim);
            } else {
                throw new IllegalStateException("Unable to acquire Redis lock to update claim");
            }
        } catch (InterruptedException e) {
            throw new IllegalStateException("Error occurred while acquiring Redis lock", e);
        } finally {
            lock.unlock();
        }
    }

    @CacheEvict(value = "claim", key = "#id")
    public void deleteClaim(Long id) {
        RLock lock = redissonClient.getLock("deleteClaimLock:" + id);
        try {
            if (lock.tryLock(10, TimeUnit.SECONDS)) {
                Claim claim = claimRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Claim not found with id: " + id));
                claimRepository.delete(claim);
            } else {
                throw new IllegalStateException("Unable to acquire Redis lock to delete claim");
            }
        } catch (InterruptedException e) {
            throw new IllegalStateException("Error occurred while acquiring Redis lock", e);
        } finally {
            lock.unlock();
        }
    }

    @CacheEvict(value = "claims", allEntries = true)
    public void evictAllClaimsCache() {
        // Clear all cached claims
    }
}
