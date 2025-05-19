package com.example.crypto.klineservice.controller.exception;

import com.example.crypto.klineservice.model.exception.InputInvalidException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class MyAppExceptionHandler {

    @ExceptionHandler({InputInvalidException.class})
    public ResponseEntity handInputInvalidException(InputInvalidException ex){
        return new ResponseEntity(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
