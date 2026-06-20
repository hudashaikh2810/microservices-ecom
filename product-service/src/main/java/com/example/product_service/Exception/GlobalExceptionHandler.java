package com.example.product_service.Exception;

import com.example.product_service.Entity.ErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {
private final static Logger logger= LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(ProductWithIdNotFound.class)
    public ResponseEntity<ErrorResponse> handleProductNotFoundException(ProductWithIdNotFound productWithIdNotFound, WebRequest webRequest) {
        var errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                Instant.now(),
                productWithIdNotFound.getMessage(),
                webRequest.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);

    }
    @ExceptionHandler(SkuWithIdNotFound.class)
    public ResponseEntity<ErrorResponse> handleProductNotFoundException(SkuWithIdNotFound productWithIdNotFound, WebRequest webRequest) {
        var errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                Instant.now(),
                productWithIdNotFound.getMessage(),
                webRequest.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);

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
    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<ErrorResponse> handleJsonProcessingException(JsonProcessingException ex, WebRequest webRequest) {
        logger.error("Caught exception: path={}, ex={}", webRequest.getDescription(false), ex.getMessage(), ex);

        var errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                Instant.now(),
                "A malformed JSON error",
                webRequest.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);

    }

}
