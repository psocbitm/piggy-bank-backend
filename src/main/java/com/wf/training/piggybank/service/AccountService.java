package com.wf.training.piggybank.service;

import com.wf.training.piggybank.exception.UserNotFoundException;
import com.wf.training.piggybank.model.Account;
import com.wf.training.piggybank.model.User;
import com.wf.training.piggybank.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public Account createAccount(Account account, Long userId) {
        Optional<User> user = userService.getUserById(userId);
        if (user.isPresent()) {
            account.setUser(user.get());
            // Implement account creation logic here, e.g., setting initial balance, generating account number, etc.
            return accountRepository.save(account);
        } else {
            throw new UserNotFoundException("User not found with ID: " + userId);
        }
    }

    public Account updateAccount(Account account) {
        return accountRepository.save(account);
    }

    public void deleteAccount(Long accountId) {
        accountRepository.deleteById(accountId);
    }

    // Add more business logic methods as needed
}
