package com.example.demo.repository;

import com.example.demo.model.SpBudget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<SpBudget, Long> {
    Optional<SpBudget> findByIdAndUserId(Long id, Long userId);

    Optional<SpBudget> findByNameAndUserId(String name, Long userId);

    List<SpBudget> findAllByUserId(Long userId);
}