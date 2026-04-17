package com.saude.saomunicipal.exception;

import com.saude.saomunicipal.dto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponseDTO> handleBusinessException(
            BusinessException ex,
            HttpServletRequest request
    ) {
        ErrorResponseDTO response = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "BUSINESS_ERROR",
                ex.getMessage(),
                request.getRequestURI(),
                List.of()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        List<String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        ErrorResponseDTO response = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "VALIDATION_ERROR",
                "Um ou mais campos estão inválidos.",
                request.getRequestURI(),
                details
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> handleJsonException(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ) {
        ErrorResponseDTO response = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "INVALID_REQUEST_BODY",
                "JSON inválido ou mal formatado.",
                request.getRequestURI(),
                List.of()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
public ResponseEntity<ErrorResponseDTO> handleGenericException(
        Exception ex,
        HttpServletRequest request
) {
    ex.printStackTrace();

    ErrorResponseDTO response = buildErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "INTERNAL_SERVER_ERROR",
            ex.getMessage() != null ? ex.getMessage() : "Erro interno no servidor.",
            request.getRequestURI(),
            List.of()
    );

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
}

    private ErrorResponseDTO buildErrorResponse(
            HttpStatus status,
            String error,
            String message,
            String path,
            List<String> details
    ) {
        return new ErrorResponseDTO(
                LocalDateTime.now(),
                status.value(),
                error,
                message,
                path,
                details
        );
    }
}