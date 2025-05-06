package com.sourcesense.smart_event_platform.exception.handler;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoWriteException;
import com.sourcesense.smart_event_platform.exception.*;
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

    @ExceptionHandler({
            EventNotFoundException.class,
            HttpRequestMethodNotSupportedException.class,
            CustomerNotFoundException.class,
            FullEventException.class,
            DuplicateWaitlistException.class,
            ReservationNotFoundException.class,
            JsonArgumentNotValidException.class,
            MethodArgumentNotValidException.class,
            ReservationNotFoundException.class,
            InvalidCredentialsException.class,
            DuplicateKeyException.class,
            MongoWriteException.class
    })
    public ResponseEntity<ProblemDetail> handleException(Exception e) {
        return handleException(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ProblemDetail> handleNullPointerException(NullPointerException e) {
        return handleException(e, HttpStatus.NO_CONTENT);
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
