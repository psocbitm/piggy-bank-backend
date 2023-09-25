package com.wf.training.piggybank.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Data
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String accountNumber;

    @ManyToOne
    private User user;

    private BigDecimal balance;

    private String accountStatus;

    // Generate getters and setters

}

