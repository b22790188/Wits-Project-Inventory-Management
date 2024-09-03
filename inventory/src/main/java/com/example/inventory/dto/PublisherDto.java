package com.example.inventory.dto;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class PublisherDto {

    @JsonProperty("publisher_id")
    private Integer publisherId;

    @NotBlank(message = "Publisher name is mandatory")
    @Size(max = 30, message = "Publisher name must be at most 30 characters")
    @JsonProperty("publisher_name")
    private String publisherName;
}