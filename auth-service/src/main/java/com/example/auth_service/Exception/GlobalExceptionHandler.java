package com.example.auth_service.Exception;

import com.example.auth_service.DTO.ErrorResponse;
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
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<?> handleInvalidToken(InvalidTokenException invalidTokenException, WebRequest webRequest)
    {
        logger.error("Caught exception: path={}, ex={}", webRequest.getDescription(false),invalidTokenException.getMessage(),invalidTokenException);
        var errorResponse=new ErrorResponse();
        errorResponse.setMessage("Your token is invalid");
        errorResponse.setPath(webRequest.getContextPath());
        errorResponse.setTimestamp(Instant.now());
        errorResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(errorResponse,HttpStatus.NOT_FOUND);

    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception ex,WebRequest webRequest)
    {
        logger.error("Caught exception: path={}, ex={}", webRequest.getDescription(false),ex.getMessage(),ex);
        var errorResponse=new ErrorResponse();
        errorResponse.setMessage("An unhandled error occur");
        errorResponse.setPath(webRequest.getContextPath());
        errorResponse.setTimestamp(Instant.now());
        errorResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new ResponseEntity<>(errorResponse,HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(TokenExpired.class)
    public ResponseEntity<?> handleTokenExpired(TokenExpired tokenExpired, WebRequest webRequest)
    {
        logger.error("Caught exception: path={}, ex={}", webRequest.getDescription(false),tokenExpired.getMessage(),tokenExpired);
        var errorResponse=new ErrorResponse();
        errorResponse.setMessage("Your token is invalid");
        errorResponse.setPath(webRequest.getContextPath());
        errorResponse.setTimestamp(Instant.now());
        errorResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(errorResponse,HttpStatus.NOT_FOUND);

    }
}
