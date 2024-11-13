package com.example.demo.model;

import com.example.demo.dto.ExpenseDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Entity
@Table(name = "sp_expense")
public class SpExpense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"expenseId\"", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "description")
    private String description;

    @Column(name = "date")
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"budgetId\"")
    @ToString.Exclude
    private SpBudget budget;

    public SpExpense(ExpenseDto dto) {
        this.id = dto.getId();
        this.name = dto.getName();
        this.amount = dto.getAmount();
        this.description = dto.getDescription();
        this.date = dto.getDate();
        this.budget = dto.getBudget() != null ?
                new SpBudget(dto.getBudget()) :
                null;
    }

}