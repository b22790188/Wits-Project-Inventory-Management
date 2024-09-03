package com.example.inventory.service;

import com.example.inventory.dto.InventoryMovementDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InventoryMovementService {

    Page<InventoryMovementDto> getAllInventoryMovements(Pageable pageable);

    Page<InventoryMovementDto> getInventoryMovementsByProductId(Integer productId, Pageable pageable);

    InventoryMovementDto getInventoryMovementById(Integer id);

    InventoryMovementDto createInventoryMovement(InventoryMovementDto inventoryMovementDto);

    InventoryMovementDto updateInventoryMovement(Integer id, InventoryMovementDto inventoryMovementDto);

    void deleteInventoryMovement(Integer id);
}