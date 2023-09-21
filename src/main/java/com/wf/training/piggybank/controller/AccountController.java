package com.wf.training.piggybank.controller;

import com.wf.training.piggybank.exception.AccountNotFoundException;
import com.wf.training.piggybank.exception.IncompleteUserDetailsException;
import com.wf.training.piggybank.exception.UserNotFoundException;
import com.wf.training.piggybank.model.Account;
import com.wf.training.piggybank.service.AccountService;
import com.wf.training.piggybank.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;
    private final UserService userService;

    @Autowired
    public AccountController(AccountService accountService, UserService userService) {
        this.accountService = accountService;
        this.userService = userService;
    }

    @GetMapping("/")
    public ResponseEntity<List<Account>> getAllAccounts() {
        List<Account> accounts = accountService.getAllAccounts();
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<Account> getAccountById(@PathVariable Long accountId) {
        Optional<Account> account = accountService.getAccountById(accountId);
        return account.map(ResponseEntity::ok)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + accountId));
    }

    @PostMapping("/{userId}")
    public ResponseEntity<Account> createAccount(@RequestBody Account account, @PathVariable Long userId) {
        try {

            if (userService.isUserDataIncomplete(userId)) {
                throw new IncompleteUserDetailsException("User details are incomplete. Account creation is not allowed.");
            }

            Account createdAccount = accountService.createAccount(account, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAccount);
        } catch (UserNotFoundException ex) {
            throw new UserNotFoundException("User not found with ID: " + userId);
        }
    }


    @PutMapping("/{accountId}")
    public ResponseEntity<Account> updateAccount(@PathVariable Long accountId, @RequestBody Account updatedAccount) {
        Optional<Account> account = accountService.getAccountById(accountId);
        if (account.isPresent()) {
            updatedAccount.setId(accountId);
            Account updatedAccountEntity = accountService.updateAccount(updatedAccount);
            return ResponseEntity.ok(updatedAccountEntity);
        } else {
            throw new AccountNotFoundException("Account not found with ID: " + accountId);
        }
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long accountId) {
        Optional<Account> account = accountService.getAccountById(accountId);
        if (account.isPresent()) {
            accountService.deleteAccount(accountId);
            return ResponseEntity.noContent().build();
        } else {
            throw new AccountNotFoundException("Account not found with ID: " + accountId);
        }
    }

    // Add more endpoints and exception handling as needed

}
