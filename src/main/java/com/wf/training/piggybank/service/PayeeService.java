package com.wf.training.piggybank.service;

import com.wf.training.piggybank.exception.PayeeNotFoundException;
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

    public List<Payee> getAllPayeesByUserId(Long userId) {
        return payeeRepository.findByUserId(userId);
    }

    public Payee createPayee(Payee payee, Long userId) {
        if (payee.getName() == null || payee.getAccountNumber() == null) {
            throw new IllegalArgumentException("Payee name and account number are required.");
        }

        // Check if the account number already exists
        if (payeeRepository.findByAccountNumber(payee.getAccountNumber()).isPresent()) {
            throw new IllegalArgumentException("Payee with account number " + payee.getAccountNumber() + " already exists.");
        }

        // Check if the user exists
        Optional<User> user = userService.getUserById(userId);
        if (user.isPresent()) {
            // Implement logic to retrieve the user's associated account
            List<Account> userAccount = accountRepository.findByUserId(userId);

            if (!userAccount.isEmpty()) {
                payee.setUser(user.get());
                return payeeRepository.save(payee);
            } else {
                throw new UserNotFoundException("User does not have an account. Cannot create payee.");
            }
        } else {
            throw new UserNotFoundException("User not found with ID: " + userId);
        }
    }

    public Payee updatePayee(Long payeeId, Payee updatedPayee) {
        // Check if the payee exists
        Optional<Payee> existingPayeeOptional = getPayeeById(payeeId);

        if (existingPayeeOptional.isPresent()) {
            Payee existingPayee = existingPayeeOptional.get();

            // Check if the provided updates are valid
            if (updatedPayee.getName() != null) {
                existingPayee.setName(updatedPayee.getName());
            }
            if (updatedPayee.getAccountNumber() != null) {
                // Check if the new account number already exists
                Optional<Payee> payeeWithNewAccountNumber = payeeRepository.findByAccountNumber(updatedPayee.getAccountNumber());
                if (payeeWithNewAccountNumber.isPresent() && !payeeWithNewAccountNumber.get().getId().equals(payeeId)) {
                    throw new IllegalArgumentException("Payee with account number " + updatedPayee.getAccountNumber() + " already exists.");
                }
                existingPayee.setAccountNumber(updatedPayee.getAccountNumber());
            }

            // Update the payee in the repository
            return payeeRepository.save(existingPayee);
        } else {
            throw new PayeeNotFoundException("Payee not found with ID: " + payeeId);
        }
    }

    public void deletePayee(Long payeeId) {
        payeeRepository.deleteById(payeeId);
    }

    public boolean isPayee(Account sourceAccount, Account destinationAccount) {
        List<Payee> payees = getAllPayeesByUserId(sourceAccount.getUser().getId());

        for (Payee payee : payees) {
            if (payee.getAccountNumber().equals(destinationAccount.getAccountNumber())) {
                return true;
            }
        }

        return false;
    }

}
