package com.example.demo.repository;

import com.example.demo.model.SpExpense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<SpExpense, Long> {
    Optional<SpExpense> findByIdAndBudgetId(Long id, Long budgetId);

    List<SpExpense> findAllByBudgetId(Long budgetId);
}