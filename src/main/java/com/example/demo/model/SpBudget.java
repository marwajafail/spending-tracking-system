package com.example.demo.model;

import com.example.demo.dto.BudgetDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Entity
@Table(name = "sp_budget")
public class SpBudget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"budgetId\"", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "\"startDate\"", nullable = false)
    private LocalDate startDate;

    @Column(name = "\"endDate\"", nullable = false)
    private LocalDate endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"categoryId\"")
    @ToString.Exclude
    private SpCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uid")
    @ToString.Exclude
    private SpUser user;

    @OneToMany(mappedBy = "budget", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<SpExpense> expenses;

    public SpBudget(BudgetDto dto) {
        this.id = dto.getId();
        this.name = dto.getName();
        this.amount = dto.getAmount();
        this.startDate = dto.getStartDate();
        this.endDate = dto.getEndDate();
        this.category = dto.getCategory() != null ?
                new SpCategory(dto.getCategory()) :
                null;
        this.user = dto.getUser() != null ?
                new SpUser(dto.getUser()) :
                null;
        this.expenses = dto.getExpenses() != null ?
                dto.getExpenses().stream().map(SpExpense::new).toList() :
                null;
    }
}