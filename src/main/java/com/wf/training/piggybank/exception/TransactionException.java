package com.wf.training.piggybank.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class TransactionException extends RuntimeException {

    public TransactionException(String message) {
        super(message);
    }
}
