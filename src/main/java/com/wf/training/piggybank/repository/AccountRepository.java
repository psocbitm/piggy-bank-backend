package com.wf.training.piggybank.repository;

import com.wf.training.piggybank.model.Account;
import com.wf.training.piggybank.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUser(User user);
    Optional<Account> findByAccountNumber(String accountNumber);
}
