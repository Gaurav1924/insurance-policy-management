package com.insurance.policy_management.controller;

import com.insurance.policy_management.model.Policy;
import com.insurance.policy_management.services.PolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/policies")
public class PolicyController {

    @Autowired
    private PolicyService policyService;

    @PostMapping
    public Policy createPolicy(@RequestBody Policy policy) {
        return policyService.createPolicy(policy);
    }

    @GetMapping
    public List<Policy> getAllPolicies() {
        return policyService.getAllPolicies();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Policy> getPolicyById(@PathVariable Long id) {
        return policyService.getPolicyById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Policy> updatePolicy(@PathVariable Long id, @RequestBody Policy policyDetails) {
        return ResponseEntity.ok(policyService.updatePolicy(id, policyDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePolicy(@PathVariable Long id) {
        policyService.deletePolicy(id);
        return ResponseEntity.noContent().build();
    }
}
