package com.insurance.policy_management.services;

import com.insurance.policy_management.model.Claim;
import com.insurance.policy_management.model.Customer;
import com.insurance.policy_management.model.Policy;
import com.insurance.policy_management.repository.ClaimRepository;
import com.insurance.policy_management.repository.PolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClaimService {

    @Autowired
    private ClaimRepository claimRepository;

    @Autowired
    private PolicyRepository policyRepository;

    public Claim fileClaim(Claim claim) {
        Optional<Policy> policyOptional = policyRepository.findById(claim.getPolicy().getId());

        if (!policyOptional.isPresent()) {
            throw new RuntimeException("Policy not found with id: " + claim.getPolicy().getId());
        }

        claim.setPolicy(policyOptional.get());
        return claimRepository.save(claim);
    }

    public List<Claim> getAllClaims() {
        return claimRepository.findAll();
    }

    public Optional<Claim> getClaimById(Long id) {
        return claimRepository.findById(id);
    }

    public Claim updateClaim(Long id, Claim claimDetails) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim not found"));
        claim.setClaimAmount(claimDetails.getClaimAmount());
        claim.setDateOfIncident(claimDetails.getDateOfIncident());
        claim.setDescription(claimDetails.getDescription());
        claim.setStatus(claimDetails.getStatus());
        return claimRepository.save(claim);
    }

    public void deleteClaim(Long id) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim not found"));
        claimRepository.delete(claim);
    }
}
