package com.wf.training.piggybank.service;

import com.wf.training.piggybank.exception.AccountNotFoundException;
import com.wf.training.piggybank.exception.UserNotFoundException;
import com.wf.training.piggybank.model.Account;
import com.wf.training.piggybank.model.User;
import com.wf.training.piggybank.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserService userService;

    @Autowired
    public AccountService(AccountRepository accountRepository, UserService userService) {
        this.accountRepository = accountRepository;
        this.userService = userService;
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Optional<Account> getAccountById(Long accountId) {
        return accountRepository.findById(accountId);
    }

    public Account createAccount(Long userId) {
        try {
            Objects.requireNonNull(userId, "UserId cannot be null");

            User user = userService.getUserById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

            Account account = new Account();
            account.setAccountNumber(generateRandomAccountNumber());
            account.setUser(user);
            account.setBalance(BigDecimal.ZERO);

            return accountRepository.save(account);
        } catch (Exception e) {
            throw e;
        }
    }

    private String generateRandomAccountNumber() {
        Random random = new Random();
        StringBuilder accountNumber = new StringBuilder();

        for (int i = 0; i < 10; i++) {
            accountNumber.append(random.nextInt(13));
        }

        return accountNumber.toString();
    }


    public Account updateAccount(Account updatedAccount) {
        Optional<Account> optionalAccount = accountRepository.findById(updatedAccount.getId());

        if (optionalAccount.isPresent()) {
            Account existingAccount = optionalAccount.get();

            if (updatedAccount.getAccountNumber() != null) {
                existingAccount.setAccountNumber(updatedAccount.getAccountNumber());
            }

            if (updatedAccount.getUser() != null) {
                existingAccount.setUser(updatedAccount.getUser());
            }

            if (updatedAccount.getBalance() != null) {
                existingAccount.setBalance(updatedAccount.getBalance());
            }

            if (updatedAccount.getAccountStatus() != null) {
                existingAccount.setAccountStatus(updatedAccount.getAccountStatus());
            }

            return accountRepository.save(existingAccount);
        } else {
            throw new AccountNotFoundException("Account not found with ID: " + updatedAccount.getId());
        }
    }

    public void deleteAccount(Long accountId) {
        accountRepository.deleteById(accountId);
    }

    public List<Account> getAllAccountsByUserId(Long userId) {
        return accountRepository.findByUserId(userId);
    }

    public Optional<Account> getAccountByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }


}
