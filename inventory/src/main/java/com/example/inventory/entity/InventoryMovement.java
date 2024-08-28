package com.example.inventory.entity;

import lombok.Data;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "inventory_movement")
@Data
public class InventoryMovement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer movementId;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Product product;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MovementType movementType;

    @Column(nullable = false)
    private Integer quantity;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp movementDate;

    @Column(columnDefinition = "TEXT")
    private String remarks;
}
