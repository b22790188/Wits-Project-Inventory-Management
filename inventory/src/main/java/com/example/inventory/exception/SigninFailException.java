package com.example.inventory.exception;

public class SigninFailException extends RuntimeException {
    public SigninFailException(String message) {
        super(message);
    }
}