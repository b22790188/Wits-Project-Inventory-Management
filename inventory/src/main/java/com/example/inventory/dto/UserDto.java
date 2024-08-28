package com.example.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    @JsonProperty("user_id")
    private Integer userId;

    @NotBlank(message = "Name is mandatory")
    @Size(max = 30, message = "Name must be at most 30 characters")
    private String name;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email must be at most 100 characters")
    private String email;
}