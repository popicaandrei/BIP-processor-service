package com.processorservice.config;

import com.processorservice.config.exceptions.EventDataException;
import com.processorservice.config.exceptions.UserAlreadyExistException;
import com.processorservice.config.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.persistence.EntityNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String userNotFoundHandler(UserNotFoundException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    String userAlreadyExistHandler(UserAlreadyExistException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(EventDataException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    String payloadDataMissingHandler(EventDataException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String entityNotFound(EntityNotFoundException ex) {
        return ex.getMessage();
    }
}
