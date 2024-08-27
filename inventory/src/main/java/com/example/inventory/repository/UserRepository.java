package com.example.inventory.repository;

import com.example.inventory.entity.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    
    boolean existsByEmail(String email);
    
    Optional<User> findByEmail(String email);
}
