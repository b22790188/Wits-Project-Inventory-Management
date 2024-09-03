package com.example.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import com.example.inventory.entity.Category;
import com.example.inventory.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    Page<Product> findByCategory(Category category, Pageable pageable);
    Page<Product> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);
}