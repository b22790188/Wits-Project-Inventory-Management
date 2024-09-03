package com.example.inventory.controller;

import com.example.inventory.dto.general.ApiResponse;
import com.example.inventory.dto.InventoryMovementDto;
import com.example.inventory.service.InventoryMovementService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/1.0/inventory")
public class InventoryMovementController {

    @Autowired
    private InventoryMovementService inventoryMovementService;

    public InventoryMovementController(InventoryMovementService inventoryMovementService){
        this.inventoryMovementService = inventoryMovementService;
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<Page<InventoryMovementDto>>> getAllInventoryMovements(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<InventoryMovementDto> inventoryMovements = inventoryMovementService.getAllInventoryMovements(pageable);
        ApiResponse<Page<InventoryMovementDto>> response = new ApiResponse<>(inventoryMovements);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/product")
    public ResponseEntity<ApiResponse<Page<InventoryMovementDto>>> getInventoryMovementsByProductId(
            @RequestParam Integer productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<InventoryMovementDto> inventoryMovements = inventoryMovementService.getInventoryMovementsByProductId(productId, pageable);
        ApiResponse<Page<InventoryMovementDto>> response = new ApiResponse<>(inventoryMovements);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/details")
    public ResponseEntity<ApiResponse<InventoryMovementDto>> getInventoryMovementById(@RequestParam Integer id) {
        InventoryMovementDto inventoryMovement = inventoryMovementService.getInventoryMovementById(id);
        ApiResponse<InventoryMovementDto> response = new ApiResponse<>(inventoryMovement);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<InventoryMovementDto>> createInventoryMovement(
            @RequestBody InventoryMovementDto inventoryMovementDto) {
        InventoryMovementDto createdMovement = inventoryMovementService.createInventoryMovement(inventoryMovementDto);
        ApiResponse<InventoryMovementDto> response = new ApiResponse<>(createdMovement);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<ApiResponse<InventoryMovementDto>> updateInventoryMovement(
            @RequestParam Integer id,
            @RequestBody InventoryMovementDto inventoryMovementDto) {
        InventoryMovementDto updatedMovement = inventoryMovementService.updateInventoryMovement(id, inventoryMovementDto);
        ApiResponse<InventoryMovementDto> response = new ApiResponse<>(updatedMovement);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteInventoryMovement(@RequestParam Integer id) {
        inventoryMovementService.deleteInventoryMovement(id);
        ApiResponse<Void> response = new ApiResponse<>(null);
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }
}