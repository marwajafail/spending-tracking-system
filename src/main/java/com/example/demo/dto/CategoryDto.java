package com.example.demo.dto;

import com.example.demo.model.SpCategory;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO for {@link com.example.demo.model.SpCategory}
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryDto implements Serializable {
    private Long id;
    private String name;
    private UserDto user;
    private List<BudgetDto> budgets;

    public CategoryDto(SpCategory category, boolean details) {
        this.id = category.getId();
        this.name = category.getName();
        this.user = category.getUser() != null ?
                new UserDto(category.getUser(), false) :
                null;
        if (details) {
            this.budgets = category.getBudgets() != null ?
                    category.getBudgets().stream().peek(budget -> budget.setCategory(null)).map(budget -> new BudgetDto(budget, false)).toList() :
                    new ArrayList<>();
        }
    }
}