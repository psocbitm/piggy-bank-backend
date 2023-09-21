package com.wf.training.piggybank.model;

import jakarta.persistence.*;
import lombok.Data;


import java.math.BigDecimal;
import java.util.Date;


@Entity
@Data
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TransactionType type; // DEPOSIT, WITHDRAWAL, or TRANSFER

    @ManyToOne
    private Account senderAccount;

    @ManyToOne
    private Account receiverAccount;

    private BigDecimal amount;
    private Date transactionDate;

}
