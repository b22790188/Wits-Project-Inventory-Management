package com.example.inventory.controller;

import com.example.inventory.dto.ProductDto;
import com.example.inventory.entity.Category;
import com.example.inventory.exception.BadRequestException;
import com.example.inventory.service.ProductService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import com.example.inventory.dto.ApiResponse;

import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/1.0/products")
@Validated
public class ProductController {

    private ProductService productService;

    public ProductController(ProductService productService){
        this.productService = productService;
    }

    @PostMapping(consumes = {"application/json"})
    public ResponseEntity<ApiResponse<String>> createProduct(@Valid @RequestBody ProductDto productDto) {
        productService.createProduct(productDto);
        ApiResponse<String> response = new ApiResponse<>("Product created successfully");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/details")
    public ResponseEntity<ApiResponse<ProductDto>> getProductById(@RequestParam(required = true) @NotNull Integer id) {
        ProductDto productDto = productService.getProductById(id);
        ApiResponse<ProductDto> response = new ApiResponse<>(productDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}