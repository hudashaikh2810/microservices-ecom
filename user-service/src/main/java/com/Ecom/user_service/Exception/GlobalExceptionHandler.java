package com.Ecom.user_service.Exception;

import com.Ecom.user_service.Dto.ErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {
private final static Logger logger= LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(UserWithIdNotFound.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserWithIdNotFound userWithIdNotFound, WebRequest webRequest) {
        logger.error("An error occured ",userWithIdNotFound.getMessage(),webRequest.getDescription(false),webRequest.getContextPath());
        var errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                Instant.now(),
                userWithIdNotFound.getMessage(),
                webRequest.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);

    }
    @ExceptionHandler(AddressWithIdNotFound.class)
    public ResponseEntity<ErrorResponse> handleAddressNotFoundException(AddressWithIdNotFound addressWithIdNotFound, WebRequest webRequest) {
        logger.error("An error occured ",addressWithIdNotFound.getMessage(),webRequest.getDescription(false),webRequest.getContextPath());

        var errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                Instant.now(),
                addressWithIdNotFound.getMessage(),
                webRequest.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);

    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityException(DataIntegrityViolationException ex, WebRequest request) {
        Throwable cause = ex.getCause(); // often a ConstraintViolationException
        var message = cause != null ? cause.getMessage() : ex.getMessage();

        var errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                Instant.now(),
                message,
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllException(Exception ex, WebRequest webRequest) {
        logger.error("Caught exception: path={}, ex={}", webRequest.getDescription(false), ex.getMessage(), ex);

        var errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                Instant.now(),
                "An unhandled error occurred",
                webRequest.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);

    }






}
