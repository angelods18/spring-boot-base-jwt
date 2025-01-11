package it.angelodesantis.controlleradvice;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import it.angelodesantis.exceptions.NotFoundException;
import it.angelodesantis.exceptions.PreconditionFailedException;
import it.angelodesantis.exceptions.ValidationFailedException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, List<String>>> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream().map(FieldError::getDefaultMessage).collect(Collectors.toList());
        return new ResponseEntity<>(getErrorsMap(errors), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, List<String>>> handleNotFoundException(NotFoundException ex) {
        List<String> errors = ex.getPayload().getErrorCodes();
        return new ResponseEntity<>(getErrorsMap(errors), new HttpHeaders(), HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(ValidationFailedException.class)
    public ResponseEntity<Map<String, List<String>>> handleValidationFailedException(ValidationFailedException ex) {
        List<String> errors = ex.getPayload().getErrorCodes();
        return new ResponseEntity<>(getErrorsMap(errors), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(PreconditionFailedException.class)
    public ResponseEntity<Map<String, List<String>>> handlePreconditionFailedException(PreconditionFailedException ex) {
        List<String> errors = ex.getPayload().getErrorCodes();
        return new ResponseEntity<>(getErrorsMap(errors), new HttpHeaders(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Map<String, List<String>>> handleGeneralExceptions(Exception ex) {
        List<String> errors = Collections.singletonList(ex.getMessage());
        return new ResponseEntity<>(getErrorsMap(errors), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public final ResponseEntity<Map<String, List<String>>> handleRuntimeExceptions(RuntimeException ex) {
    	ex.printStackTrace();
        List<String> errors = Collections.singletonList(ex.getMessage());
        return new ResponseEntity<>(getErrorsMap(errors), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Map<String, List<String>> getErrorsMap(List<String> errors) {
        Map<String, List<String>> errorResponse = new HashMap<>();
        errorResponse.put("errors", errors);
        return errorResponse;
    }

}