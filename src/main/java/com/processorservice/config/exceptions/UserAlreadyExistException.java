package com.processorservice.config.exceptions;

public class UserAlreadyExistException extends RuntimeException {

    public UserAlreadyExistException(String message) {
       super(message);
    }
}
