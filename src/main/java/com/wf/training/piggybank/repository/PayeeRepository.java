package com.wf.training.piggybank.repository;

import com.wf.training.piggybank.model.Payee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PayeeRepository extends JpaRepository<Payee, Long> {
    Optional<Object> findByAccountNumber(String accountNumber);
}
