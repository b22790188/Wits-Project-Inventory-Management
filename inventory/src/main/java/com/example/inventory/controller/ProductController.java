package com.example.inventory.controller;

import com.example.inventory.dto.ProductDto;
import com.example.inventory.dto.general.ApiResponse;
import com.example.inventory.entity.Category;
import com.example.inventory.exception.BadRequestException;
import com.example.inventory.service.ProductService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

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

    @GetMapping("/{category}")
    public ResponseEntity<ApiResponse<Page<ProductDto>>> getProducts(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDto> products;

        if ("all".equalsIgnoreCase(category)) {
            products = productService.getAllProducts(pageable);
        } else {
            try {
                Category categoryEnum = Category.valueOf(category.toUpperCase());
                products = productService.getProductsByCategory(categoryEnum, pageable);
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid category");
            }
        }

        ApiResponse<Page<ProductDto>> response = new ApiResponse<>(products);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<ProductDto>>> searchProducts(
            @RequestParam("keyword") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDto> products = productService.searchProductsByTitle(keyword, pageable);
        ApiResponse<Page<ProductDto>> response = new ApiResponse<>(products);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<ApiResponse<ProductDto>> updateProduct(
            @RequestParam @NotNull Integer id,
            @RequestBody @Valid ProductDto productDto) {
        
        try{
            ProductDto updatedProduct = productService.updateProduct(id, productDto);
            ApiResponse<ProductDto> response = new ApiResponse<>(updatedProduct);
            return ResponseEntity.ok(response);
        }catch (Exception e) {
            String errorMessage = e.getMessage();
            if (errorMessage != null && errorMessage.contains("Duplicate entry")) {
                if (errorMessage.contains("for key 'book.isbn'")) {
                    throw new BadRequestException("ISBN already exists");
                }
            }
            throw new BadRequestException(errorMessage);
        }
    }
    

    @DeleteMapping
    public ResponseEntity<ApiResponse<String>> deleteProduct(@RequestParam(required = true) @NotNull Integer id) {
        productService.deleteProduct(id);
        ApiResponse<String> response = new ApiResponse<>("Product deleted successfully");
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }
}