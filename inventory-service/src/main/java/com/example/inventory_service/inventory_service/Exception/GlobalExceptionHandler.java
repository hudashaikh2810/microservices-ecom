package com.example.inventory_service.inventory_service.Exception;

import com.example.inventory_service.inventory_service.DTO.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.ConcurrentModificationException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(InventoryWithSkuNotPresent.class)
    public ResponseEntity<ErrorResponse> handleInventoryNotFoundException(InventoryWithSkuNotPresent inventoryWithSkuNotFound, WebRequest webRequest) {
        var errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                Instant.now(),
                inventoryWithSkuNotFound.getMessage(),
                webRequest.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);

    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException illegalArgumentException, WebRequest webRequest) {
        var errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                Instant.now(),
                illegalArgumentException.getMessage(),
                webRequest.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);

    }
    @ExceptionHandler(ReservationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleReservationNotFoundException(ReservationNotFoundException reservationNotFoundException, WebRequest webRequest) {
        var errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                Instant.now(),
                reservationNotFoundException.getMessage(),
                webRequest.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);

    }
    @ExceptionHandler(ReservationExpiredException.class)
    public ResponseEntity<ErrorResponse> handleReservationNotFoundException(ReservationExpiredException reservationExpiredException, WebRequest webRequest) {
        var errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                Instant.now(),
                reservationExpiredException.getMessage(),
                webRequest.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);

    }
    @ExceptionHandler(InvalidOrderConfirmationException.class)
    public ResponseEntity<ErrorResponse> handleInvalidOrderConfirmationException(InvalidOrderConfirmationException invalidOrderConfirmationException, WebRequest webRequest) {
        var errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                Instant.now(),
                invalidOrderConfirmationException.getMessage(),
                webRequest.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);

    }
    @ExceptionHandler(InventoryInconsistencyException.class)
    public ResponseEntity<ErrorResponse> handleInventoryInconsistencyException(InventoryInconsistencyException inventoryInconsistencyException, WebRequest webRequest) {
        var errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                Instant.now(),
                inventoryInconsistencyException.getMessage(),
                webRequest.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);

    }

    @ExceptionHandler(OrderAlreadyProcessed.class)
    public ResponseEntity<ErrorResponse> handleOrderAlreadyProcessed(OrderAlreadyProcessed orderAlreadyProcessed, WebRequest webRequest) {
        var errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                Instant.now(),
                orderAlreadyProcessed.getMessage(),
                webRequest.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);

    }


    @ExceptionHandler(ConcurrentModificationException.class)
    public ResponseEntity<ErrorResponse> handleConcurrentModificationException(ConcurrentModificationException concurrentModificationException, WebRequest webRequest) {
        var errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                Instant.now(),
                concurrentModificationException.getMessage(),
                webRequest.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);

    }


}
