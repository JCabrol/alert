package com.safetynet.alert.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.sql.SQLException;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)

@ControllerAdvice
public class AlertExceptionHandler extends ResponseEntityExceptionHandler {

    private ResponseEntity<Object> buildResponseEntity(AlertError alertError) {
        return new ResponseEntity<>(alertError.getMessage(), alertError.getStatus());
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String errorMessage = "The request is not correct: please verify the request's body.\n";
        log.error(errorMessage);
        return buildResponseEntity(new AlertError(HttpStatus.BAD_REQUEST, errorMessage));
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String errorMessage = "The request is not correct : please verify the request's url.\n";
        log.error(errorMessage);
        return buildResponseEntity(new AlertError(HttpStatus.BAD_REQUEST, errorMessage));
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String errorMessage = "The request is not correct : the request's body should be in json.\n";
        log.error(errorMessage);
        return buildResponseEntity(new AlertError(HttpStatus.BAD_REQUEST, errorMessage));
    }

    @ExceptionHandler(PersonNotFoundException.class)
    protected ResponseEntity<Object> handlePersonNotFound(
            PersonNotFoundException ex) {
        AlertError alertError = new AlertError(NOT_FOUND);
        alertError.setMessage(ex.getMessage());
        log.error(ex.getMessage());
        return buildResponseEntity(alertError);
    }

    @ExceptionHandler(PersonAlreadyExistingException.class)
    protected ResponseEntity<Object> handlePersonAlreadyExisting(
            PersonAlreadyExistingException ex) {
        AlertError alertError = new AlertError(BAD_REQUEST);
        alertError.setMessage(ex.getMessage());
        log.error(ex.getMessage());
        return buildResponseEntity(alertError);
    }

    @ExceptionHandler(EmptyPersonsException.class)
    protected ResponseEntity<Object> handleEmptyPersons(
            EmptyPersonsException ex) {
        AlertError alertError = new AlertError(NOT_FOUND);
        alertError.setMessage(ex.getMessage());
        log.error(ex.getMessage());
        return buildResponseEntity(alertError);
    }

    @ExceptionHandler(SQLException.class)
    protected ResponseEntity<Object> handleSQL(
            SQLException ex) {
        AlertError alertError = new AlertError(NOT_FOUND);
        String errorMessage = "Something went wrong with database: " + ex.getMessage().substring(0, ex.getMessage().indexOf('\n')) + "\n";
        alertError.setMessage(errorMessage);
        log.error(ex.getMessage());
        return buildResponseEntity(alertError);
    }

    @ExceptionHandler(NothingToUpdateException.class)
    protected ResponseEntity<Object> handleNothingToUpdate(
            NothingToUpdateException ex) {
        AlertError alertError = new AlertError(BAD_REQUEST);
        alertError.setMessage(ex.getMessage());
        log.error(ex.getMessage());
        return buildResponseEntity(alertError);
    }

    @ExceptionHandler(NotTheSamePersonException.class)
    protected ResponseEntity<Object> handleNotTheSamePerson(
            NotTheSamePersonException ex) {
        AlertError alertError = new AlertError(CONFLICT);
        alertError.setMessage(ex.getMessage());
        log.error(ex.getMessage());
        return buildResponseEntity(alertError);
    }

    @ExceptionHandler(FirestationNotFoundException.class)
    protected ResponseEntity<Object> handleFirestationNotFound(
            FirestationNotFoundException ex) {
        AlertError alertError = new AlertError(NOT_FOUND);
        alertError.setMessage(ex.getMessage());
        log.error(ex.getMessage());
        return buildResponseEntity(alertError);
    }

    @ExceptionHandler(EmptyFirestationsException.class)
    protected ResponseEntity<Object> handleEmptyFirestations(
            EmptyFirestationsException ex) {
        AlertError alertError = new AlertError(NOT_FOUND);
        alertError.setMessage(ex.getMessage());
        log.error(ex.getMessage());
        return buildResponseEntity(alertError);
    }

    @ExceptionHandler(MappingAlreadyExistingException.class)
    protected ResponseEntity<Object> handleMappingAlreadyExisting(
            MappingAlreadyExistingException ex) {
        AlertError alertError = new AlertError(BAD_REQUEST);
        alertError.setMessage(ex.getMessage());
        log.error(ex.getMessage());
        return buildResponseEntity(alertError);
    }

    @ExceptionHandler(FirestationNonEmptyException.class)
    protected ResponseEntity<Object> handleFirestationNonEmpty(
            FirestationNonEmptyException ex) {
        AlertError alertError = new AlertError(BAD_REQUEST);
        alertError.setMessage(ex.getMessage());
        log.error(ex.getMessage());
        return buildResponseEntity(alertError);
    }

    @ExceptionHandler(NothingToDeleteException.class)
    protected ResponseEntity<Object> handleNothingToDelete(
            NothingToDeleteException ex) {
        AlertError alertError = new AlertError(BAD_REQUEST);
        alertError.setMessage(ex.getMessage());
        log.error(ex.getMessage());
        return buildResponseEntity(alertError);
    }

    @ExceptionHandler(NotRightFormatToPostException.class)
    protected ResponseEntity<Object> handleNotRightFormatToPost(
            NotRightFormatToPostException ex) {
        AlertError alertError = new AlertError(BAD_REQUEST);
        alertError.setMessage(ex.getMessage());
        log.error(ex.getMessage());
        return buildResponseEntity(alertError);
    }

    @ExceptionHandler(EmptyMedicalRecordsException.class)
    protected ResponseEntity<Object> handleEmptyMedicalRecords(
            EmptyMedicalRecordsException ex) {
        AlertError alertError = new AlertError(NOT_FOUND);
        alertError.setMessage(ex.getMessage());
        log.error(ex.getMessage());
        return buildResponseEntity(alertError);
    }

    @ExceptionHandler(MedicalRecordsNotFoundException.class)
    protected ResponseEntity<Object> handleMedicalRecordsNotFound(
            MedicalRecordsNotFoundException ex) {
        AlertError alertError = new AlertError(NOT_FOUND);
        alertError.setMessage(ex.getMessage());
        log.error(ex.getMessage());
        return buildResponseEntity(alertError);
    }

    @ExceptionHandler(AddressNotFoundException.class)
    protected ResponseEntity<Object> handleAddressNotFound(
            AddressNotFoundException ex) {
        AlertError alertError = new AlertError(NOT_FOUND);
        alertError.setMessage(ex.getMessage());
        log.error(ex.getMessage());
        return buildResponseEntity(alertError);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<Object> handleConstraintViolation(
            ConstraintViolationException ex) {
        AlertError alertError = new AlertError(BAD_REQUEST);
        String message = "There is something wrong in the given data :\n" + ex.getConstraintViolations().stream().map(constraintViolation -> "- " + constraintViolation.getMessageTemplate() + "\n").collect(Collectors.joining());
        alertError.setMessage(message);
        log.error(message);
        return buildResponseEntity(alertError);
    }
}

