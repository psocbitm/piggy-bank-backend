package com.wf.training.piggybank.controller;

import com.wf.training.piggybank.exception.*;
import com.wf.training.piggybank.model.Transaction;
import com.wf.training.piggybank.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/")
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long transactionId) {
        try {
            Transaction transaction = transactionService.getTransactionById(transactionId);
            return ResponseEntity.ok(transaction);
        } catch (TransactionNotFoundException e) {
            throw new TransactionNotFoundException("Transaction not found with ID: " + transactionId);
        }
    }

    @PostMapping("/")
    public ResponseEntity<String> createTransaction(@RequestBody Transaction transaction) {
        if (transaction.getType() == null) {
            return ResponseEntity.badRequest().body("Transaction type is required. Please provide a valid transaction type. (DEPOSIT, WITHDRAWAL, TRANSFER)");
        }

        try {
            transactionService.createTransaction(transaction);
            return ResponseEntity.ok("Transaction created successfully.");
        } catch (TransactionException e) {
            throw new TransactionException("Transaction failed: " + e.getMessage());
        }
    }

}
