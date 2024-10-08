package com.example.inventory.exception.notfound;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(Integer productId) {
        super("Product not found with id: " + productId);
    }
}
