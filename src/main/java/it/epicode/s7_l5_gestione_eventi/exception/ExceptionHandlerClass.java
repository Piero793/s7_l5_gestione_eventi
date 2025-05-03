package it.epicode.s7_l5_gestione_eventi.exception;


import it.epicode.s7_l5_gestione_eventi.security.Exceptions;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ExceptionHandlerClass extends ResponseEntityExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> entityNotFound(EntityNotFoundException ex) {
        return new ResponseEntity<>("Entity not found | " + ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exceptions.CredenzialiNonValideException.class)
    public ResponseEntity<String> handleCredenzialiNonValideException(Exceptions.CredenzialiNonValideException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exceptions.UtenteGiaEsistenteException.class)
    public ResponseEntity<String> handleUtenteGiaEsistenteException(Exceptions.UtenteGiaEsistenteException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT); // 409 Conflict
    }

    @ExceptionHandler(Exceptions.AutorizzazioneNegataException.class)
    public ResponseEntity<String> handleAutorizzazioneNegataException(Exceptions.AutorizzazioneNegataException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN); // 403 Forbidden
    }

    @ExceptionHandler(Exceptions.UtenteNonTrovatoException.class)
    public ResponseEntity<String> handleUtenteNonTrovatoException(Exceptions.UtenteNonTrovatoException ex) {
        return new ResponseEntity<>("Utente non trovato | " + ex.getMessage(), HttpStatus.NOT_FOUND); // 404 Not Found
    }

    // validazione da service
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String fieldName = violation.getPropertyPath().toString();
            if (fieldName.contains(".")) {
                fieldName = fieldName.substring(fieldName.lastIndexOf('.') + 1);
            }
            errors.put(fieldName, violation.getMessage());
        }
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // validazione da controller
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        for (var error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), "Errore controller: " + error.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}