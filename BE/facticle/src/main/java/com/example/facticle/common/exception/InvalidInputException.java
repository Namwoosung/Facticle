package com.example.facticle.common.exception;

import java.util.Map;

//DB 확인 후 발생하는 에러를 처리
public class InvalidInputException extends RuntimeException{
    private final Map<String, String> errors;

    public InvalidInputException(String message, Map<String, String> errors){
        super(message);
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
