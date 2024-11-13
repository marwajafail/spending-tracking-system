package com.example.demo.dto;

import com.example.demo.model.SpBudget;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO for {@link com.example.demo.model.SpBudget}
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BudgetDto implements Serializable {
    private Long id;
    private String name;
    private Double amount;
    private LocalDate startDate;
    private LocalDate endDate;
    private CategoryDto category;
    private UserDto user;
    private List<ExpenseDto> expenses;

    public BudgetDto(SpBudget budget, boolean details) {
        this.id = budget.getId();
        this.name = budget.getName();
        this.amount = budget.getAmount();
        this.startDate = budget.getStartDate();
        this.endDate = budget.getEndDate();
        this.category = budget.getCategory() != null ?
                new CategoryDto(budget.getCategory(), false) :
                null;
        this.user = budget.getUser() != null ?
                new UserDto(budget.getUser(), false) :
                null;
        if (details) {
            this.expenses = budget.getExpenses() != null ?
                    budget.getExpenses().stream().peek(expense -> expense.setBudget(null)).map(ExpenseDto::new).toList() :
                    new ArrayList<>();
        }
    }
}