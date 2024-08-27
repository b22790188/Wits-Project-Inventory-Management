package com.example.inventory.controller;

import com.example.inventory.dto.AuthorDto;
import com.example.inventory.service.AuthorService;

import jakarta.validation.constraints.NotNull;

import com.example.inventory.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/1.0/author")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AuthorDto>> createAuthor(@RequestBody AuthorDto authorDto) {
        AuthorDto createdAuthor = authorService.createAuthor(authorDto);
        ApiResponse<AuthorDto> response = new ApiResponse<>(createdAuthor);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<AuthorDto>>> getAllAuthors() {
        List<AuthorDto> authors = authorService.getAllAuthors();
        ApiResponse<List<AuthorDto>> response = new ApiResponse<>(authors);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/details")
    public ResponseEntity<ApiResponse<AuthorDto>> getAuthorById(@RequestParam(required = true) @NotNull Integer id) {
        AuthorDto authorDto = authorService.getAuthorById(id);
        ApiResponse<AuthorDto> response = new ApiResponse<>(authorDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<ApiResponse<AuthorDto>> updateAuthor(@RequestParam(required = true) @NotNull Integer id, @RequestBody AuthorDto authorDto) {
        AuthorDto updatedAuthor = authorService.updateAuthor(id, authorDto);
        ApiResponse<AuthorDto> response = new ApiResponse<>(updatedAuthor);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAuthor(@RequestParam(required = true) @NotNull Integer id) {
        authorService.deleteAuthor(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}