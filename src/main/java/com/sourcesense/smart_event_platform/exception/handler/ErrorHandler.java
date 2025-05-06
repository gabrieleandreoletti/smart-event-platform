package com.sourcesense.smart_event_platform.exception.handler;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoWriteException;
import com.sourcesense.smart_event_platform.exception.CustomerNotFoundException;
import com.sourcesense.smart_event_platform.exception.EventNotFoundException;
import com.sourcesense.smart_event_platform.exception.InvalidCredentialsException;
import com.sourcesense.smart_event_platform.exception.JsonArgumentNotValidException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(MongoWriteException.class)
    public ResponseEntity<ProblemDetail> handleMongoWriteException(MongoWriteException e) {
        return handleException(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleEventNotFoundException(EventNotFoundException e) {
        return handleException(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ProblemDetail> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return handleException(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleCustomerNotFoundException(CustomerNotFoundException e) {
        return handleException(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ProblemDetail> handleNullPointerException(NullPointerException e) {
        return handleException(e, HttpStatus.NO_CONTENT);
    }


    @ExceptionHandler(JsonArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleJsonArgumentNotValidException(JsonArgumentNotValidException e) {
        JsonArgumentNotValidException argumentNotValidException = new JsonArgumentNotValidException("Il body inserito nella richiesta non rispetta gli standard di validazione : " + e.getLocalizedMessage(), e);

        return handleException(argumentNotValidException, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        JsonArgumentNotValidException argumentNotValidException = new JsonArgumentNotValidException("Il body inserito nella richiesta non rispetta gli standard di validazione : " + e.getLocalizedMessage(), e);

        return handleException(argumentNotValidException, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ProblemDetail> handleInvalidCredentialsException(InvalidCredentialsException e) {
        return handleException(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ProblemDetail> handleDuplicateKeyException(DuplicateKeyException e) {
        return handleException(e, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenericException(Exception e) {
        return handleException(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    private ResponseEntity<ProblemDetail> handleException(Throwable throwable, HttpStatus status) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(status);
        problemDetail.setType(URI.create("http://api.sourcense/error/" + status.name()));
        problemDetail.setDetail(throwable.getMessage());
        problemDetail.setProperty("cause", throwable.getClass());
        return ResponseEntity.badRequest().body(problemDetail);
    }
}
