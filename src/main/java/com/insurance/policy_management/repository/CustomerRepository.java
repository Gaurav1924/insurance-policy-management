package com.insurance.policy_management.repository;

import com.insurance.policy_management.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByContactInformation(String contactInformation);
}
