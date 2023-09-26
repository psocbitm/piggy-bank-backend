package com.wf.training.piggybank.controller;

import com.wf.training.piggybank.model.Transaction;
import com.wf.training.piggybank.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @PostMapping("/transfer")
    public ResponseEntity<Transaction> performTransfer(@RequestBody Transaction transaction) {
        Transaction createdTransaction = transactionService.performTransfer(transaction);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTransaction);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Transaction> performWithdrawal(@RequestBody Transaction transaction) {
        Transaction createdTransaction = transactionService.performWithdrawal(transaction);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTransaction);
    }

    @PostMapping("/deposit")
    public ResponseEntity<Transaction> performDeposit(@RequestBody Transaction transaction) {
        Transaction createdTransaction = transactionService.performDeposit(transaction);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTransaction);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Transaction>> getAllTransactionsByUser(@PathVariable Long userId) {
        List<Transaction> transactions = transactionService.getAllTransactionsByUser(userId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/")
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }
}
