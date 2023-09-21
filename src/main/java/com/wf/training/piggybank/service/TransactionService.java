package com.wf.training.piggybank.service;

import com.wf.training.piggybank.exception.*;
import com.wf.training.piggybank.model.Account;
import com.wf.training.piggybank.model.Transaction;
import com.wf.training.piggybank.model.TransactionType;
import com.wf.training.piggybank.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, AccountService accountService) {
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Transaction getTransactionById(Long transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found with ID: " + transactionId));
    }

    @Transactional(rollbackFor = TransactionException.class)
    public void createTransaction(Transaction transaction) {
        try {
            TransactionType type = transaction.getType();

            if (type == null) {
                throw new InvalidTransactionTypeException("Transaction type is required.");
            }

            switch (type) {
                case DEPOSIT:
                    if (transaction.getReceiverAccount() == null) {
                        throw new ReceiverAccountRequiredException("Receiver account is required for a DEPOSIT transaction.");
                    }
                    deposit(transaction);
                    break;

                case WITHDRAWAL:
                    if (transaction.getSenderAccount() == null) {
                        throw new SenderAccountRequiredException("Sender account is required for a WITHDRAWAL transaction.");
                    }
                    withdrawal(transaction);
                    break;

                case TRANSFER:
                    if (transaction.getSenderAccount() == null || transaction.getReceiverAccount() == null) {
                        throw new SenderReceiverAccountsRequiredException("Both sender and receiver accounts are required for a TRANSFER transaction.");
                    }
                    transfer(transaction);
                    break;

                default:
                    throw new InvalidTransactionTypeException("Invalid transaction type.");
            }

            // Set transaction date and save it
            transaction.setTransactionDate(new Date());
            transactionRepository.save(transaction);
        } catch (TransactionException e) {
            throw new TransactionException("Transaction failed: " + e.getMessage());
        }
    }

    private void deposit(Transaction transaction) {
        Account receiverAccount = accountService.getAccountById(transaction.getReceiverAccount().getId())
                .orElseThrow(() -> new AccountNotFoundException("Receiver account not found."));

        BigDecimal transactionAmount = transaction.getAmount();
        BigDecimal receiverBalance = receiverAccount.getBalance().add(transactionAmount);
        receiverAccount.setBalance(receiverBalance);
        accountService.updateAccount(receiverAccount);
    }

    private void withdrawal(Transaction transaction) {
        Account senderAccount = accountService.getAccountById(transaction.getSenderAccount().getId())
                .orElseThrow(() -> new AccountNotFoundException("Sender account not found."));

        BigDecimal transactionAmount = transaction.getAmount();

        if (senderAccount.getBalance().compareTo(transactionAmount) < 0) {
            throw new InsufficientBalanceException("Sender doesn't have sufficient balance.");
        }

        BigDecimal senderBalance = senderAccount.getBalance().subtract(transactionAmount);
        senderAccount.setBalance(senderBalance);
        accountService.updateAccount(senderAccount);
    }

    private void transfer(Transaction transaction) {
        Account senderAccount = accountService.getAccountById(transaction.getSenderAccount().getId())
                .orElseThrow(() -> new AccountNotFoundException("Sender account not found."));

        Account receiverAccount = accountService.getAccountById(transaction.getReceiverAccount().getId())
                .orElseThrow(() -> new AccountNotFoundException("Receiver account not found."));

        BigDecimal transactionAmount = transaction.getAmount();

        if (senderAccount.getBalance().compareTo(transactionAmount) < 0) {
            throw new InsufficientBalanceException("Sender doesn't have sufficient balance.");
        }

        BigDecimal senderBalance = senderAccount.getBalance().subtract(transactionAmount);
        BigDecimal receiverBalance = receiverAccount.getBalance().add(transactionAmount);

        senderAccount.setBalance(senderBalance);
        receiverAccount.setBalance(receiverBalance);

        accountService.updateAccount(senderAccount);
        accountService.updateAccount(receiverAccount);
    }
}
