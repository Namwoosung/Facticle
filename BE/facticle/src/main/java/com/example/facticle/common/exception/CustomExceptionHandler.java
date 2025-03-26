package com.example.facticle.common.exception;

import com.example.facticle.common.dto.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler {
    //validation failed
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

    //DB 조회 결과 잘못된 입력인 경우
    @ExceptionHandler(InvalidInputException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse invalidInputException(InvalidInputException ex){
        log.warn("Invalid input error: {}", ex.getErrors());

        Map<String, Object> data = new HashMap<>();
        data.put("code", 400);
        data.put("errors", ex.getErrors());

        return BaseResponse.failure(data, "Invalid input");
    }

    //인증 과정에서 실패한 경우
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public BaseResponse badCredentialsException(BadCredentialsException ex){
        log.warn("Authentication Failed: {}", ex.getMessage());

        Map<String, Object> data = new HashMap<>();
        data.put("code", 401);
        data.put("error", "username or password invalid");

        return BaseResponse.failure(data, "Authentication failed. Please check your credentials.");
    }

    //인증 및 인가 과정에 대한 일반적인 에러의 경우
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public BaseResponse authenticationException(AuthenticationException ex) {
        log.warn("Authentication Failed (General): {}", ex.getMessage());

        Map<String, Object> data = new HashMap<>();
        data.put("code", 401);
        data.put("error", ex.getMessage());

        return BaseResponse.failure(data, "Authentication failed. Please try again.");
    }

    @ExceptionHandler(MissingRequestCookieException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse missingCookieException(MissingRequestCookieException ex){
        log.warn("Missing required cookie: {}", ex.getCookieName());

        Map<String, Object> data = new HashMap<>();
        data.put("code", 400);
        data.put("error", ex.getCookieName() + " is required");

        return BaseResponse.failure(data, "Missing required cookie.");
    }

    @ExceptionHandler(ExpiredTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public BaseResponse expiredTokenException(ExpiredTokenException ex){
        log.warn("expired refresh token");

        Map<String, Object> data = new HashMap<>();
        data.put("code", 401);
        data.put("is_expired", true);

        return BaseResponse.failure(data, ex.getMessage());
    }

    @ExceptionHandler(InvalidTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public BaseResponse invalidTokenException(InvalidTokenException ex){
        log.warn("invalid refresh token");

        Map<String, Object> data = new HashMap<>();
        data.put("code", 401);
        data.put("is_expired", false);

        return BaseResponse.failure(data, ex.getMessage());
    }

    @ExceptionHandler(OAuthException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public BaseResponse oAuthException(OAuthException ex){
        log.warn("OAuth error: {}", ex.getMessage());

        Map<String, Object> data = new HashMap<>();
        data.put("code", 401);
        data.put("error", ex.getMessage());

        return BaseResponse.failure(data, "OAuth authentication failed");
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
