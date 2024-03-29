package com.wf.training.piggybank.service;

import com.wf.training.piggybank.exception.*;
import com.wf.training.piggybank.model.Account;
import com.wf.training.piggybank.model.Transaction;
import com.wf.training.piggybank.model.TransactionType;
import com.wf.training.piggybank.model.UserStatus;
import com.wf.training.piggybank.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountService accountService;

    private final PayeeService payeeService;
    @Autowired
    public TransactionService(TransactionRepository transactionRepository, AccountService accountService, PayeeService payeeService) {
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
        this.payeeService = payeeService;
    }

    public Transaction performTransfer(Transaction transaction) {
        if (transaction.getType() != TransactionType.TRANSFER_RTGS && transaction.getType() != TransactionType.TRANSFER_NEFT) {
            throw new InvalidTransactionTypeException("Invalid transaction type for transfer");
        }
        String sourceAccountId = transaction.getSourceAccount().getAccountNumber();
        String destinationAccountId = transaction.getDestinationAccount().getAccountNumber();
        Optional<Account> sourceAccountOpt = accountService.getAccountByAccountNumber(sourceAccountId);
        Optional<Account> destinationAccountOpt = accountService.getAccountByAccountNumber(destinationAccountId);

        if (transaction.getType() == TransactionType.TRANSFER_RTGS) {
            if (destinationAccountOpt.isEmpty()) {
                throw new DestinationAccountNotFoundException("Destination account not found for RTGS transaction");
            }
        }

        if (sourceAccountOpt.isEmpty()) {
            throw new SourceAccountNotFoundException("Source account not found");
        }



        Account sourceAccount = sourceAccountOpt.get();
        if(sourceAccount.getUser().getUserStatus()== UserStatus.LOCKED){
            throw new UserLockedException("User is locked");
        }
        BigDecimal amount = transaction.getAmount();

        if (sourceAccount.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance in source account");
        }

        BigDecimal newSourceAccountBalance = sourceAccount.getBalance().subtract(amount);
        sourceAccount.setBalance(newSourceAccountBalance);
        accountService.updateAccount(sourceAccount);

        if (transaction.getType() != TransactionType.TRANSFER_RTGS) {
            if (destinationAccountOpt.isEmpty()) {
                Transaction newTransaction = new Transaction();
                newTransaction.setSourceAccount(sourceAccount);
                newTransaction.setType(TransactionType.TRANSFER_NEFT);
                newTransaction.setAmount(amount);

                return transactionRepository.save(newTransaction);
            }
        }

        Account destinationAccount = destinationAccountOpt.orElse(null);

        if (destinationAccount != null && !payeeService.isPayee(sourceAccount, destinationAccount)) {
            throw new DestinationNotPayeeException("Destination account is not a payee of source account");
        }

        if (destinationAccount != null) {
            BigDecimal newDestinationAccountBalance = destinationAccount.getBalance().add(amount);
            destinationAccount.setBalance(newDestinationAccountBalance);
            accountService.updateAccount(destinationAccount);
        }

        Transaction newTransaction = new Transaction();
        newTransaction.setSourceAccount(sourceAccount);
        newTransaction.setDestinationAccount(destinationAccount);
        newTransaction.setType(transaction.getType());
        newTransaction.setAmount(amount);

        return transactionRepository.save(newTransaction);
    }



    public Transaction performWithdrawal(Transaction transaction) {
        Long sourceAccountId = transaction.getSourceAccount().getId();
        Optional<Account> sourceAccount = accountService.getAccountById(sourceAccountId);

        if (sourceAccount.isPresent()) {
            if(sourceAccount.get().getUser().getUserStatus()== UserStatus.LOCKED){
                throw new UserLockedException("User is locked");
            }
            if (sourceAccount.get().getBalance().compareTo(transaction.getAmount()) < 0) {
                throw new InsufficientBalanceException("Insufficient balance in source account");
            }

            BigDecimal newSourceAccountBalance = sourceAccount.get().getBalance().subtract(transaction.getAmount());
            sourceAccount.get().setBalance(newSourceAccountBalance);
            accountService.updateAccount(sourceAccount.get());

            Transaction newTransaction = new Transaction();
            newTransaction.setSourceAccount(sourceAccount.get());
            newTransaction.setType(TransactionType.WITHDRAWAL);
            newTransaction.setAmount(transaction.getAmount());
            return transactionRepository.save(newTransaction);
        } else {
            throw new SourceAccountNotFoundException("Source account not found");
        }
    }

    public Transaction performDeposit(Transaction transaction) {
        Long destinationAccountId = transaction.getDestinationAccount().getId();
        Optional<Account> destinationAccountOpt = accountService.getAccountById(destinationAccountId);

        if (destinationAccountOpt.isEmpty()) {
            throw new DestinationAccountNotFoundException("Destination account not found");
        }
        if(destinationAccountOpt.get().getUser().getUserStatus()== UserStatus.LOCKED){
            throw new UserLockedException("User is locked");
        }

        Account destinationAccount = destinationAccountOpt.get();

        BigDecimal amount = transaction.getAmount();
        BigDecimal newDestinationAccountBalance = destinationAccount.getBalance().add(amount);
        destinationAccount.setBalance(newDestinationAccountBalance);

        accountService.updateAccount(destinationAccount);

        Transaction newTransaction = new Transaction();
        newTransaction.setDestinationAccount(destinationAccount);
        newTransaction.setType(TransactionType.DEPOSIT);
        newTransaction.setAmount(amount);

        return transactionRepository.save(newTransaction);
    }


    public List<Transaction> getAllTransactionsByUser(Long userId) {
        return transactionRepository.findAllBySourceAccountUserIdOrDestinationAccountUserId(userId, userId);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
}
