package com.example.inventory.repository;

import com.example.inventory.entity.Author;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Integer> {
    List<Author> findByAuthorNameContainingIgnoreCaseOrBioContainingIgnoreCase(String authorNameKeyword, String bioKeyword);
}