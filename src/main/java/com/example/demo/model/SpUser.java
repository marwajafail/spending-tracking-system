package com.example.demo.model;

import com.example.demo.dto.UserDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Entity
@Table(name = "sp_user")
public class SpUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uid", nullable = false)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "password", nullable = false)
    private String password;

    //Used if admin deactivates account
    @Column(name = "status")
    private Boolean status;

    @Column(name = "verification_code", length = 64)
    private String verificationCode;

    //Used for user verification of account
    @Column(name = "isEnabled")
    private boolean enabled;

    @Column(name = "resetToken")
    private String resetToken;

    @Column(name = "tokenExpirationTime")
    private LocalDateTime tokenExpirationTime;

    @ManyToOne
    @JoinColumn(name = "\"roleId\"")
    @ToString.Exclude
    private SpRole role;

    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "\"profileId\"")
    @ToString.Exclude
    private SpProfile profile;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<SpBudget> budgets;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<SpCategory> categories;

    public SpUser(UserDto dto) {
        this.id = dto.getId();
        this.email = dto.getEmail();
        this.password = dto.getPassword();
        this.status = dto.getStatus();
        this.role = dto.getRole() != null ?
                new SpRole(dto.getRole()) :
                null;
        this.profile = dto.getProfile() != null ?
                new SpProfile(dto.getProfile()) :
                null;
        this.budgets = dto.getBudgets() != null ?
                dto.getBudgets().stream().map(SpBudget::new).toList() :
                null;
        this.categories = dto.getCategories() != null ?
                dto.getCategories().stream().map(SpCategory::new).toList() :
                null;
        this.verificationCode = dto.getVerificationCode();
        this.enabled = dto.isEnabled();
        this.resetToken = dto.getResetToken();
        this.tokenExpirationTime = dto.getTokenExpirationTime();
    }
}