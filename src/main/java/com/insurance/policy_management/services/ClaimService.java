package com.insurance.policy_management.services;

import com.insurance.policy_management.exceptions.ResourceNotFoundException;
import com.insurance.policy_management.model.Claim;
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
            throw new ResourceNotFoundException("Policy not found with id: " + claim.getPolicy().getId());
        }

        claim.setPolicy(policyOptional.get());
        return claimRepository.save(claim);
    }

    public List<Claim> getAllClaims() {
        return claimRepository.findAll();
    }


    public Optional<Claim> getClaimById(Long id) {
        return Optional.ofNullable(claimRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found with id: " + id)));
    }

    public Claim updateClaim(Long id, Claim claimDetails) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found with id: " + id));
        claim.setClaimAmount(claimDetails.getClaimAmount());
        claim.setDateOfIncident(claimDetails.getDateOfIncident());
        claim.setDescription(claimDetails.getDescription());
        claim.setStatus(claimDetails.getStatus());
        return claimRepository.save(claim);
    }

    public void deleteClaim(Long id) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found with id: " + id));
        claimRepository.delete(claim);
    }
}
