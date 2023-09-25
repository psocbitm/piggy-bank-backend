package com.wf.training.piggybank.service;

import com.wf.training.piggybank.exception.UserNotFoundException;
import com.wf.training.piggybank.model.Account;
import com.wf.training.piggybank.model.User;
import com.wf.training.piggybank.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserService userService; // Inject the UserService

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
            account.setBalance(BigDecimal.ZERO); // Assuming balance is initialized to zero

            return accountRepository.save(account);
        } catch (Exception e) {
            throw e;
        }
    }

    private String generateRandomAccountNumber() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10);
    }


    public Account updateAccount(Account account) {
        return accountRepository.save(account);
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


    // Add more business logic methods as needed
}
