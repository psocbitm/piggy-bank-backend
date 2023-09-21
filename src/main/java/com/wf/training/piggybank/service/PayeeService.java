package com.wf.training.piggybank.service;


import com.wf.training.piggybank.exception.UserNotFoundException;
import com.wf.training.piggybank.model.Account;
import com.wf.training.piggybank.model.Payee;
import com.wf.training.piggybank.model.User;
import com.wf.training.piggybank.repository.AccountRepository;
import com.wf.training.piggybank.repository.PayeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PayeeService {

    private final PayeeRepository payeeRepository;

    private final AccountRepository accountRepository;
    private final UserService userService;

    @Autowired
    public PayeeService(PayeeRepository payeeRepository, UserService userService, AccountRepository accountRepository) {
        this.payeeRepository = payeeRepository;
        this.accountRepository = accountRepository;
        this.userService = userService;
    }

    public List<Payee> getAllPayees() {
        return payeeRepository.findAll();
    }

    public Optional<Payee> getPayeeById(Long payeeId) {
        return payeeRepository.findById(payeeId);
    }

    public Payee createPayee(Payee payee, Long userId) {
        if (payee.getName() == null || payee.getAccountNumber() == null) {
            throw new IllegalArgumentException("Payee name and account number are required.");
        }

        // Check if the account number already exists
        if(payeeRepository.findByAccountNumber(payee.getAccountNumber()).isPresent()) {
            throw new IllegalArgumentException("Payee with account number " + payee.getAccountNumber() + " already exists.");
        }

        // Check if the user exists
        Optional<User> user = userService.getUserById(userId);
        if (user.isPresent()) {
            // Implement logic to retrieve the user's associated account
            Optional<Account> userAccount = accountRepository.findByUser(user.get());

            // Check if the user has an associated account
            if (userAccount.isPresent()) {
                payee.setUser(user.get());
                return payeeRepository.save(payee);
            } else {
                throw new UserNotFoundException("User does not have an account. Cannot create payee.");
            }
        } else {
            throw new UserNotFoundException("User not found with ID: " + userId);
        }
    }



    public Payee updatePayee(Payee payee) {
        return payeeRepository.save(payee);
    }

    public void deletePayee(Long payeeId) {
        payeeRepository.deleteById(payeeId);
    }
}
