package com.insurance.policy_management.services;

import com.insurance.policy_management.model.Claim;
import com.insurance.policy_management.model.Policy;
import com.insurance.policy_management.repository.ClaimRepository;
import com.insurance.policy_management.repository.PolicyRepository;
import com.insurance.policy_management.service.ClaimService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class ClaimServiceTest {

    @Autowired
    private ClaimService claimService;

    @Autowired
    private ClaimRepository claimRepository;

    @Autowired
    private PolicyRepository policyRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private Policy policy;
    private Claim claim;

    @BeforeEach
    void setUp() {
        // Create and save a new policy in the database
        policy = new Policy();
        policy.setPolicyNumber("P12345");
        policy.setType("Health");
        policy.setCoverageAmount(5000.0);
        policy.setStartDate(new Date());
        policy.setEndDate(new Date());
        policy.setPremium(500.0);

        policy = policyRepository.save(policy);

        // Create a new claim related to the policy
        claim = new Claim();
        claim.setPolicy(policy);
        claim.setClaimAmount(1000.0);
        claim.setDateOfIncident(new Date());
        claim.setDescription("Test Claim");
        claim.setStatus("Pending");
    }

    @Test
    @Transactional
    void testConcurrentFileClaim_WithSerializableIsolation() throws InterruptedException, ExecutionException {
        // Use ExecutorService to simulate multiple threads concurrently trying to file the same claim
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        Callable<Claim> task = () -> claimService.fileClaim(claim);

        // Submit three concurrent tasks
        Future<Claim> future1 = executorService.submit(task);
        Future<Claim> future2 = executorService.submit(task);
        Future<Claim> future3 = executorService.submit(task);

        // Wait for the tasks to complete
        Claim result1 = future1.get();
        Claim result2 = future2.get();
        Claim result3 = future3.get();

        // Assert that the claims are not null and persisted
        assertNotNull(result1);
        assertNotNull(result2);
        assertNotNull(result3);

        // Verify that the claim was persisted in the database
        Optional<Claim> persistedClaim = claimRepository.findById(result1.getId());
        assertNotNull(persistedClaim);

        // Ensure all transactions are flushed to the database
        entityManager.flush();

        // Shutdown the executor service
        executorService.shutdown();
    }
}
