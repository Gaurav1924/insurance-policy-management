package com.insurance.policy_management.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "claims")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Claim implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double claimAmount;

    @Temporal(TemporalType.DATE)
    private Date dateOfIncident;

    private String description;
    private String status;

    @JoinColumn(name = "policy_id", nullable = false)
    private Long policyId;  // Store the policy_id directly as a Long
}
