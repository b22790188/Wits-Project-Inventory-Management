package com.example.inventory.service;

import com.example.inventory.dto.AuthorDto;

import java.util.List;

public interface AuthorService {

    AuthorDto createAuthor(AuthorDto authorDto);

    AuthorDto getAuthorById(Integer id);

    List<AuthorDto> getAllAuthors();

    List<AuthorDto> searchAuthors(String keyword);

    AuthorDto updateAuthor(Integer id, AuthorDto authorDto);

    void deleteAuthor(Integer id);
}