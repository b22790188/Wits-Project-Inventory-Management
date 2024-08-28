package com.example.inventory.dto;

import com.example.inventory.entity.Category;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

    @JsonProperty("product_id")
    private Integer productId;

    @JsonProperty("title")
    @NotBlank(message = "Title is mandatory")
    @Size(max = 100, message = "Title must be at most 100 characters")
    private String title;

    @JsonProperty("author_id")
    @NotNull(message = "Author ID is mandatory")
    private Integer authorId;

    @JsonProperty("publisher_id")
    @NotNull(message = "Publisher ID is mandatory")
    private Integer publisherId;

    @JsonProperty("published_date")
    @PastOrPresent(message = "Published date must be in the past or present")
    private Date publishedDate;

    @JsonProperty("isbn")
    @NotBlank(message = "ISBN is mandatory")
    @Size(max = 20, message = "ISBN must be at most 20 characters")
    private String isbn;

    @JsonProperty("category")
    @NotNull(message = "Category is mandatory")
    private Category category;

    @JsonProperty("price")
    @DecimalMin(value = "0.0", inclusive = true, message = "Price must be positive")
    @Digits(integer = 8, fraction = 2, message = "Price must be a valid decimal number with up to two decimal places")
    private BigDecimal price;

    @JsonProperty("quantity")
    @Min(value = 0, message = "Quantity must be zero or positive")
    private Integer quantity = 0;

    @JsonProperty("description")
    @Size(max = 500, message = "Description must be at most 500 characters")
    private String description;
}
