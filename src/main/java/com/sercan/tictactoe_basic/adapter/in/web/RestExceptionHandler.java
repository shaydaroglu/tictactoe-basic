package com.sercan.tictactoe_basic.adapter.in.web;

import com.sercan.tictactoe_basic.adapter.in.web.dto.response.ErrorResponseDto;
import com.sercan.tictactoe_basic.application.exception.GameAlreadyFinishedException;
import com.sercan.tictactoe_basic.application.exception.GameNotFoundException;
import com.sercan.tictactoe_basic.application.exception.InvalidTurnException;
import com.sercan.tictactoe_basic.application.exception.PositionAlreadyTakenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class RestExceptionHandler {
    @ExceptionHandler(GameNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleGameNotFoundException(GameNotFoundException e) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(GameAlreadyFinishedException.class)
    public ResponseEntity<ErrorResponseDto> handleGameAlreadyFinishedException(GameAlreadyFinishedException e) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(InvalidTurnException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidTurnException(InvalidTurnException e) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(PositionAlreadyTakenException.class)
    public ResponseEntity<ErrorResponseDto> handlePositionAlreadyTakenException(PositionAlreadyTakenException e) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .orElse("Validation failed");

        return buildErrorResponse(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponseDto> handleOptimisticLocking() {
        return buildErrorResponse(
                HttpStatus.CONFLICT,
                "Game was updated concurrently. Please try again."
        );
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDto> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        String message = "Invalid request body";

        if (ex.getMostSpecificCause() != null) {
            String causeMessage = ex.getMostSpecificCause().getMessage();

            if (causeMessage != null && causeMessage.contains("Symbol")) {
                message = "playerOneSymbol must be one of: X, O";
            }
        }

        return buildErrorResponse(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(Exception ex) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    private ResponseEntity<ErrorResponseDto> buildErrorResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(
                new ErrorResponseDto(
                        Instant.now(),
                        status.value(),
                        status.getReasonPhrase(),
                        message
                )
        );
    }
}
