package com.example.facticle.common.exception;

//간단한 exception 생성
public class ExpiredTokenException extends RuntimeException{
    public ExpiredTokenException(String message) {
        super(message);
    }
}
