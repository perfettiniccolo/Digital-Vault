package it.io.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

//Controlla a livello globale
@ControllerAdvice
public class GlobalExceptionHandler extends RuntimeException {
    //Gestisce specificatamente l'errore ResourseNotFound
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> resourseNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                new Date(),
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }


    //Gestisce errori generici all'interno del server
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> globalExceptionHandler(Exception ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                new Date(),
                "Errore interno al server",
                request.getDescription(false)
        );
        //Restituisce codcie HTTP 500 (Internet Server Error)
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
