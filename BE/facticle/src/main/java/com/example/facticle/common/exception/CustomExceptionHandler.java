package com.example.facticle.common.exception;

import com.example.facticle.common.dto.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        Map<String, Object> data = new HashMap<>();
        data.put("code", 400);
        data.put("errors", errors);

        return BaseResponse.failure(data, "Validation failed.");
    }

    @ExceptionHandler(InvalidInputException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse invalidInputException(InvalidInputException ex){
        log.warn("Invalid input error: {}", ex.getErrors());

        Map<String, Object> data = new HashMap<>();
        data.put("code", 400);
        data.put("errors", ex.getErrors());

        return BaseResponse.failure(data, ex.getMessage());

    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public BaseResponse defaultException(Exception ex){
        log.error("Unhandled error: ", ex);

        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());

        Map<String, Object> data = new HashMap<>();
        data.put("code", 500);
        data.put("errors", errors);

        return BaseResponse.failure(data, "Unprocessed error");
    }
}
