package com.order_service.order_servie.Exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.order_service.order_servie.DTO.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.ConcurrentModificationException;

@RestControllerAdvice
public class GlobalExceptionHandler {
private final static Logger logger= LoggerFactory.getLogger(GlobalExceptionHandler.class);
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
    @ExceptionHandler(OrderWithIdNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrderNotFoundException(OrderWithIdNotFoundException orderWithIdNotFound, WebRequest webRequest) {
        var errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                Instant.now(),
                orderWithIdNotFound.getMessage(),

                webRequest.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);

    }
    @ExceptionHandler(OrderAlreadyCancelled.class)
    public ResponseEntity<ErrorResponse> handleOrderNotFoundException(OrderAlreadyCancelled orderAlreadyCancelled, WebRequest webRequest) {
        var errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                Instant.now(),
                orderAlreadyCancelled.getMessage(),

                webRequest.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);

    }

    @ExceptionHandler(InventoryWithSkuNotPresent.class)
    public ResponseEntity<ErrorResponse> handleInventoryWithSkuNotFoundException(InventoryWithSkuNotPresent inventoryWithIdNotFound, WebRequest webRequest) {
        var errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                Instant.now(),
                inventoryWithIdNotFound.getMessage(),
                webRequest.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);

    }

    @ExceptionHandler(OrderCouldNotBeCreated.class)
    public ResponseEntity<ErrorResponse> handleOrderNotCreatedException(OrderCouldNotBeCreated orderCouldNotBeCreated, WebRequest webRequest) {
        var errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                Instant.now(),
                orderCouldNotBeCreated.getMessage(),
                webRequest.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);

    }


    @ExceptionHandler(ProductPriceNotFound.class)
    public ResponseEntity<ErrorResponse> handleProductPriceNotFoundException(RuntimeException ex, WebRequest webRequest) {
        logger.error("Caught exception: path={}, ex={}", webRequest.getDescription(false), ex.getMessage(), ex);

        var errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                Instant.now(),
                ex.getMessage(),
                webRequest.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);

    }
    @ExceptionHandler(ItemOutOfStockException.class)
    public ResponseEntity<ErrorResponse> handleItemOutOfStockException(RuntimeException ex, WebRequest webRequest) {
        logger.error("Caught exception: path={}, ex={}", webRequest.getDescription(false), ex.getMessage(), ex);

        var errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                Instant.now(),
                ex.getMessage(),
                webRequest.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);

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
