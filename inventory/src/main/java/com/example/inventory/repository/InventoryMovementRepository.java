package com.example.inventory.repository;

import com.example.inventory.entity.InventoryMovement;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Integer> {

    Page<InventoryMovement> findByProduct_ProductId(Integer productId, Pageable pageable);

    void deleteByProduct_ProductId(Integer productId);
}