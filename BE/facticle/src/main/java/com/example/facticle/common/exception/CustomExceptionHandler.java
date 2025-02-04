package com.example.facticle.common.exception;

import com.example.facticle.common.dto.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class CustomExceptionHandler {

    //추후 검증 후 개선
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

        Map<String, Object> data = new HashMap<>();
        data.put("code", 400);
        data.put("errors", ex.getErrors());

        return BaseResponse.failure(data, ex.getMessage());

    }
}
