package com.insurance.policy_management.repository;

import com.insurance.policy_management.model.Policy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PolicyRepository extends JpaRepository<Policy, Long> {
}
