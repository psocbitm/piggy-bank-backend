package com.wf.training.piggybank.service;

import com.wf.training.piggybank.exception.DestinationAccountNotFoundException;
import com.wf.training.piggybank.exception.DestinationNotPayeeException;
import com.wf.training.piggybank.exception.InsufficientBalanceException;
import com.wf.training.piggybank.exception.SourceAccountNotFoundException;
import com.wf.training.piggybank.model.Account;
import com.wf.training.piggybank.model.Transaction;
import com.wf.training.piggybank.model.TransactionType;
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

//        Optional<Account> sourceAccountOpt = accountService.getAccountById(sourceAccountId);
//        Optional<Account> destinationAccountOpt = accountService.getAccountById(destinationAccountId);
//
//        if (sourceAccountOpt.isEmpty()) {
//            throw new SourceAccountNotFoundException("Source account not found");
//        }
//
//        Account sourceAccount = sourceAccountOpt.get();
//
//        if (destinationAccountOpt.isEmpty()) {
//            // Handle the case where the destination account is not found
//            BigDecimal amount = transaction.getAmount();
//            if (sourceAccount.getBalance().compareTo(amount) < 0) {
//                throw new InsufficientBalanceException("Insufficient balance in source account");
//            }
//
//            BigDecimal newSourceAccountBalance = sourceAccount.getBalance().subtract(amount);
//            sourceAccount.setBalance(newSourceAccountBalance);
//            accountService.updateAccount(sourceAccount);
//
//            Transaction newTransaction = new Transaction();
//            newTransaction.setSourceAccount(sourceAccount);
//            newTransaction.setType(TransactionType.TRANSFER);
//            newTransaction.setAmount(amount);
//
//            return transactionRepository.save(newTransaction);
//        }
//
//
        String sourceAccountId = transaction.getSourceAccount().getAccountNumber();
        String destinationAccountId = transaction.getDestinationAccount().getAccountNumber();
        Optional<Account> sourceAccountOpt = accountService.getAccountByAccountNumber(sourceAccountId);
        Optional<Account> destinationAccountOpt = accountService.getAccountByAccountNumber(destinationAccountId);

        if (sourceAccountOpt.isEmpty()) {
            throw new SourceAccountNotFoundException("Source account not found");
        }
        Account sourceAccount = sourceAccountOpt.get();
        if (destinationAccountOpt.isEmpty()) {
            BigDecimal amount = transaction.getAmount();
            if (sourceAccount.getBalance().compareTo(amount) < 0) {
                throw new InsufficientBalanceException("Insufficient balance in source account");
            }

            BigDecimal newSourceAccountBalance = sourceAccount.getBalance().subtract(amount);
            sourceAccount.setBalance(newSourceAccountBalance);
            accountService.updateAccount(sourceAccount);

            Transaction newTransaction = new Transaction();
            newTransaction.setSourceAccount(sourceAccount);
            newTransaction.setType(TransactionType.TRANSFER);
            newTransaction.setAmount(amount);

            return transactionRepository.save(newTransaction);
        }
        Account destinationAccount = destinationAccountOpt.get();

        if (!payeeService.isPayee(sourceAccount, destinationAccount)) {
            throw new DestinationNotPayeeException("Destination account is not a payee of source account");
        }

        BigDecimal amount = transaction.getAmount();

        if (sourceAccount.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance in source account");
        }

        BigDecimal newSourceAccountBalance = sourceAccount.getBalance().subtract(amount);
        BigDecimal newDestinationAccountBalance = destinationAccount.getBalance().add(amount);

        sourceAccount.setBalance(newSourceAccountBalance);
        destinationAccount.setBalance(newDestinationAccountBalance);

        accountService.updateAccount(sourceAccount);
        accountService.updateAccount(destinationAccount);

        Transaction newTransaction = new Transaction();
        newTransaction.setSourceAccount(sourceAccount);
        newTransaction.setDestinationAccount(destinationAccount);
        newTransaction.setType(TransactionType.TRANSFER);
        newTransaction.setAmount(amount);

        return transactionRepository.save(newTransaction);
    }


    public Transaction performWithdrawal(Transaction transaction) {
        Long sourceAccountId = transaction.getSourceAccount().getId();
        Optional<Account> sourceAccount = accountService.getAccountById(sourceAccountId);

        if (sourceAccount.isPresent()) {
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
}
