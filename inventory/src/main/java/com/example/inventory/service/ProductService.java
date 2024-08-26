package com.example.inventory.service;

import com.example.inventory.dto.ProductDto;
import com.example.inventory.entity.Category;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    
    void createProduct(ProductDto productDto);

    // ProductDto getProductById(Integer id);

    // Page<ProductDto> getAllProducts(Pageable pageable);

    // Page<ProductDto> getProductsByCategory(Category category, Pageable pageable);

    // Page<ProductDto> searchProductsByTitle(String keyword, Pageable pageable);

    // ProductDto updateProductPartially(Integer id, Map<String, Object> updates);

    // void deleteProduct(Integer id);
}
