package com.example.inventory.exception.notfound;

public class InventoryMovementNotFoundException extends RuntimeException {
    public InventoryMovementNotFoundException(Integer InventoryMovementId) {
        super("Product not found with id: " + InventoryMovementId);
    }
}