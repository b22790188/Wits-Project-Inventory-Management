package com.example.inventory.exception;

public class AuthorNotFoundException extends RuntimeException {
    public AuthorNotFoundException(Integer authorId) {
        super("Author not found with id: " + authorId);
    }
}