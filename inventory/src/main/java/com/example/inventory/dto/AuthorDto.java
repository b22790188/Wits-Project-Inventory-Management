package com.example.inventory.dto;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class AuthorDto {
    
    @JsonProperty("author_id")
    private Integer authorId;

    @JsonProperty("author_name")
    @NotBlank(message = "Author name is mandatory")
    @Size(max = 30, message = "Author name must be at most 30 characters")
    private String authorName;

    private String bio;
}