package com.safetynet.alert.exceptions;

public class PersonAlreadyExistingException extends RuntimeException {
    public PersonAlreadyExistingException(String message) {
        super(message);
    }
}
