package com.insurance.policy_management.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "policies")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Policy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String policyNumber;
    private String type;
    private Double coverageAmount;
    private Date startDate;
    private Date endDate;
    private Double premium;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)

    private Customer customer;
}
