package com.wf.training.piggybank.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IncompleteUserDetailsException extends RuntimeException {

    public IncompleteUserDetailsException(String message) {
        super(message);
    }
}
