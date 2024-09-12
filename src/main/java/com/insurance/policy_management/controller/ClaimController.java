package com.insurance.policy_management.controller;

import com.insurance.policy_management.dto.ApiResponse;
import com.insurance.policy_management.model.Claim;
import com.insurance.policy_management.service.ClaimService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/claims")
public class ClaimController {

    @Autowired
    private ClaimService claimService;

    @PostMapping
    public ResponseEntity<ApiResponse> fileClaim(@Valid @RequestBody Claim claim, BindingResult result) {
        if (result.hasErrors()) {
            String errorMessage = result.getFieldError().getDefaultMessage();
            return ResponseEntity.badRequest().body(new ApiResponse(false, errorMessage));
        }

        Claim createdClaim = claimService.fileClaim(claim);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "Claim filed successfully", createdClaim));
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllClaims() {
        List<Claim> claims = claimService.getAllClaims();
        if (claims.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse(true, "No claims found"));
        }
        return ResponseEntity.ok(new ApiResponse(true, "Claims retrieved successfully", claims));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getClaimById(@PathVariable Long id) {
        Optional<Claim> claim = claimService.getClaimById(id);
        if (claim.isPresent()) {
            return ResponseEntity.ok(new ApiResponse(true, "Claim retrieved successfully", claim.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "Claim not found"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateClaim(@PathVariable Long id, @Valid @RequestBody Claim claimDetails, BindingResult result) {
        if (result.hasErrors()) {
            String errorMessage = result.getFieldError().getDefaultMessage();
            return ResponseEntity.badRequest().body(new ApiResponse(false, errorMessage));
        }

        Optional<Claim> existingClaim = claimService.getClaimById(id);
        if (existingClaim.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "Claim not found"));
        }

        Claim updatedClaim = claimService.updateClaim(id, claimDetails);
        return ResponseEntity.ok(new ApiResponse(true, "Claim updated successfully", updatedClaim));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteClaim(@PathVariable Long id) {
        Optional<Claim> claim = claimService.getClaimById(id);
        if (claim.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "Claim not found"));
        }

        claimService.deleteClaim(id);
        return ResponseEntity.ok(new ApiResponse(true, "Claim deleted successfully"));
    }
}
