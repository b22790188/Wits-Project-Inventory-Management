package com.example.inventory.exception.notfound;

public class PublisherNotFoundException extends RuntimeException {
    public PublisherNotFoundException(Integer publisherId) {
        super("Publisher not found with id: " + publisherId);
    }
}