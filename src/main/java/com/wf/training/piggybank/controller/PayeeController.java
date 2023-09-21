package com.wf.training.piggybank.controller;

import com.wf.training.piggybank.exception.PayeeNotFoundException;
import com.wf.training.piggybank.exception.UserNotFoundException;
import com.wf.training.piggybank.model.Payee;
import com.wf.training.piggybank.service.PayeeService;
import com.wf.training.piggybank.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/payees")
public class PayeeController {

    private final PayeeService payeeService;
    private final UserService userService;

    @Autowired
    public PayeeController(PayeeService payeeService, UserService userService) {
        this.payeeService = payeeService;
        this.userService = userService;
    }

    @GetMapping("/")
    public ResponseEntity<List<Payee>> getAllPayees() {
        List<Payee> payees = payeeService.getAllPayees();
        return ResponseEntity.ok(payees);
    }

    @GetMapping("/{payeeId}")
    public ResponseEntity<Payee> getPayeeById(@PathVariable Long payeeId) {
        Optional<Payee> payee = payeeService.getPayeeById(payeeId);
        return payee.map(ResponseEntity::ok)
                .orElseThrow(() -> new PayeeNotFoundException("Payee not found with ID: " + payeeId));
    }

    @PostMapping("/{userId}")
    public ResponseEntity<Payee> createPayee(@RequestBody Payee payee, @PathVariable Long userId) {
        try {
            Payee createdPayee = payeeService.createPayee(payee, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPayee);
        } catch (UserNotFoundException ex) {
            throw new UserNotFoundException("User not found with ID: " + userId);
        }
    }

    @PutMapping("/{payeeId}")
    public ResponseEntity<Payee> updatePayee(@PathVariable Long payeeId, @RequestBody Payee updatedPayee) {
        Optional<Payee> payee = payeeService.getPayeeById(payeeId);
        if (payee.isPresent()) {
            updatedPayee.setId(payeeId);
            Payee updatedPayeeEntity = payeeService.updatePayee(updatedPayee);
            return ResponseEntity.ok(updatedPayeeEntity);
        } else {
            throw new PayeeNotFoundException("Payee not found with ID: " + payeeId);
        }
    }

    @DeleteMapping("/{payeeId}")
    public ResponseEntity<Void> deletePayee(@PathVariable Long payeeId) {
        Optional<Payee> payee = payeeService.getPayeeById(payeeId);
        if (payee.isPresent()) {
            payeeService.deletePayee(payeeId);
            return ResponseEntity.noContent().build();
        } else {
            throw new PayeeNotFoundException("Payee not found with ID: " + payeeId);
        }
    }
}
