package com.insurance.policy_management.repository;

import com.insurance.policy_management.model.Claim;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClaimRepository extends JpaRepository<Claim, Long> {
}
