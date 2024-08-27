package com.example.inventory.service.implement;

import com.example.inventory.dto.AuthorDto;
import com.example.inventory.entity.Author;
import com.example.inventory.exception.AuthorNotFoundException;
import com.example.inventory.repository.AuthorRepository;
import com.example.inventory.service.AuthorService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorServiceImpl(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Override
    public AuthorDto createAuthor(AuthorDto authorDto) {
        authorDto.setAuthorId(null);
        Author author = convertToEntity(authorDto);
        Author savedAuthor = authorRepository.save(author);
        return convertToDto(savedAuthor);
    }

    @Override
    public AuthorDto getAuthorById(Integer id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new AuthorNotFoundException(id));
        return convertToDto(author);
    }

    @Override
    public List<AuthorDto> getAllAuthors() {
        return authorRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public AuthorDto updateAuthor(Integer id, AuthorDto authorDto) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new AuthorNotFoundException(id));

        author.setAuthorName(authorDto.getAuthorName());
        author.setBio(authorDto.getBio());

        Author updatedAuthor = authorRepository.save(author);
        return convertToDto(updatedAuthor);
    }

    @Override
    public void deleteAuthor(Integer id) {
        authorRepository.deleteById(id);
    }

    private Author convertToEntity(AuthorDto authorDto) {
        Author author = new Author();
        author.setAuthorId(authorDto.getAuthorId());
        author.setAuthorName(authorDto.getAuthorName());
        author.setBio(authorDto.getBio());
        return author;
    }

    private AuthorDto convertToDto(Author author) {
        AuthorDto authorDto = new AuthorDto();
        authorDto.setAuthorId(author.getAuthorId());
        authorDto.setAuthorName(author.getAuthorName());
        authorDto.setBio(author.getBio());
        return authorDto;
    }
}
