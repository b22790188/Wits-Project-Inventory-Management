package com.example.inventory.dto;

import com.example.inventory.entity.MovementType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class InventoryMovementDto {

    @JsonProperty("movement_id")
    private Integer movementId;

    @JsonProperty("product_id")
    private Integer productId;

    @JsonProperty("movement_type")
    private MovementType movementType;

    @JsonProperty("quantity")
    private Integer quantity;

    @JsonProperty("remarks")
    private String remarks;

    @JsonProperty("movement_date")
    private Timestamp movementDate;

    @JsonProperty("sum_quantity")
    private Integer sumQuantity;
}