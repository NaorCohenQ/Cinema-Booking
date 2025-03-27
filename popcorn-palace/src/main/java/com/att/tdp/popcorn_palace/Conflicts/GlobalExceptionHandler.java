package com.att.tdp.popcorn_palace.Conflicts;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.validator.constraints.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 400 - Bad Request for validation issues (like @Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationErrors(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Invalid input");

        return ResponseEntity.badRequest().body("Validation error: " + message);
    }

    // 400 - Bad Request for illegal arguments in service layer
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    // 404 - Resource not found
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    // 400 - Parameter type mismatch
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.badRequest()
                .body("Invalid input for '" + ex.getName() + "'. Expected a number, but got: '" + ex.getValue() + "'. You should enter a number.");
    }

    // 500 - unexpected errors
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllExceptions(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An unexpected error occurred: " + ex.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleInvalidFormat(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getCause();

        if (cause instanceof InvalidFormatException ife) {
            String field = ife.getPath().get(0).getFieldName();
            Class<?> targetType = ife.getTargetType();

            if (targetType.equals(UUID.class)) {
                return ResponseEntity.badRequest().body(ErrorMessages.UUID_INVALID);
            } else if (targetType.equals(Integer.class) || targetType.equals(int.class) || targetType.equals(double.class)) {
                return ResponseEntity.badRequest().body(ErrorMessages.NUMBER_FORMAT_ERROR + field);
            } else if (targetType.equals(String.class)) {
                return ResponseEntity.badRequest().body(ErrorMessages.TEXT_FORMAT_ERROR + field);
            }
        }

        return ResponseEntity.badRequest().body(ErrorMessages.INVALID_JSON);
    }

    // 409
        @ExceptionHandler(ConflictException.class)
    public ResponseEntity<String> conflictException(ConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }


}
