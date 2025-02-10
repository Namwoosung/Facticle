package com.example.facticle.common.exception;

import lombok.Getter;

import java.util.Map;

//DB 확인 후 발생하는 에러를 처리
@Getter
public class InvalidInputException extends RuntimeException {
    private final Map<String, String> errors;

    public InvalidInputException(String message, Map<String, String> errors) {
        super(message);
        this.errors = errors;
    }
}
