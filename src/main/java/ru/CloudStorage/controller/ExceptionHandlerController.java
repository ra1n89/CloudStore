package ru.CloudStorage.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.CloudStorage.exception.CustomIoException;
import ru.CloudStorage.exception.DatabaseException;
import ru.CloudStorage.exception.ExceptionResponse;
import ru.CloudStorage.exception.MinioFileUploadException;

@ControllerAdvice
public class ExceptionHandlerController extends ResponseEntityExceptionHandler {
    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity handlerException(DatabaseException e) {
        ExceptionResponse response = new ExceptionResponse(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MinioFileUploadException.class)
    public ResponseEntity<?> handleMinioFileUploadException(MinioFileUploadException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"error\": \"" + e.getMessage() + "\"}");
    }

    @ExceptionHandler(CustomIoException.class)
    public ResponseEntity<?> handleCustomIoException(CustomIoException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"error\": \"" + e.getMessage() + "\"}");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"error\": \"Unknown error occurred\"}");
    }
}
