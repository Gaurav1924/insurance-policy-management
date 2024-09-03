package com.insurance.policy_management.repository;

import com.insurance.policy_management.model.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface PolicyRepository extends JpaRepository<Policy, Long> {
    @Query("SELECT p FROM Policy p WHERE p.startDate >= :startDate AND p.endDate <= :endDate")
    List<Policy> findByDateRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
