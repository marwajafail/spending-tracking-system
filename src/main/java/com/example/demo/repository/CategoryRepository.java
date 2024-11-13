package com.example.demo.repository;

import com.example.demo.model.SpCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<SpCategory, Long> {
    Optional<SpCategory> findByIdAndUserIdOrUserId(Long id, Long userId, Long adminId);

    Optional<SpCategory> findByIdAndUserId(Long id, Long userId);

    Optional<SpCategory> findByNameAndUserId(String categoryName, Long userId);

    Optional<SpCategory> findByNameAndUserIdOrUserId(String categoryName, Long userId, Long adminId);

    List<SpCategory> findAllByUserIdOrUserId(Long userId, Long adminId);
}