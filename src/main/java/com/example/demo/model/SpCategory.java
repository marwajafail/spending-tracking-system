package com.example.demo.model;

import com.example.demo.dto.CategoryDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Entity
@Table(name = "sp_category")
public class SpCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"categoryId\"", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uid")
    @ToString.Exclude
    private SpUser user;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<SpBudget> budgets;

    public SpCategory(CategoryDto dto) {
        this.id = dto.getId();
        this.name = dto.getName();
        this.user = dto.getUser() != null ?
                new SpUser(dto.getUser()) :
                null;
        this.budgets = dto.getBudgets() != null ?
                dto.getBudgets().stream().map(SpBudget::new).toList() :
                null;
    }
}