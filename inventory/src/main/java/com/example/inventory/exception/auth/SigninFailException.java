package com.example.inventory.exception.auth;

public class SigninFailException extends RuntimeException {
    public SigninFailException(String message) {
        super(message);
    }
}