package com.example.inventory.exception.notfound;

public class AuthorNotFoundException extends RuntimeException {
    public AuthorNotFoundException(Integer authorId) {
        super("Author not found with id: " + authorId);
    }
}