package com.wf.training.piggybank.repository;

import com.wf.training.piggybank.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllBySourceAccountUserIdOrDestinationAccountUserId(Long userId1, Long userId2);
}
