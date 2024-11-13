package com.example.demo.dto;

import com.example.demo.model.SpExpense;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link com.example.demo.model.SpExpense}
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExpenseDto implements Serializable {
    private Long id;
    private String name;
    private Double amount;
    private String description;
    private LocalDate date;
    private BudgetDto budget;

    public ExpenseDto(SpExpense expense) {
        this.id = expense.getId();
        this.name = expense.getName();
        this.amount = expense.getAmount();
        this.description = expense.getDescription();
        this.date = expense.getDate();
        this.budget = expense.getBudget() != null ?
                new BudgetDto(expense.getBudget(), false) :
                null;
    }
}