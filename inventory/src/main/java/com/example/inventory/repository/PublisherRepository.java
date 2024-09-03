package com.example.inventory.repository;

import com.example.inventory.entity.Publisher;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublisherRepository extends JpaRepository<Publisher, Integer> {
    List<Publisher> findByPublisherNameContainingIgnoreCase(String keyword);
}
