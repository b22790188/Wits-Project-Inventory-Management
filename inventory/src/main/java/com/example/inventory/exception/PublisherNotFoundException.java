package com.example.inventory.exception;

public class PublisherNotFoundException extends RuntimeException {
    public PublisherNotFoundException(Integer publisherId) {
        super("Publisher not found with id: " + publisherId);
    }
}