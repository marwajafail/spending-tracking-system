package com.example.demo.dto;

import com.example.demo.model.SpUser;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO for {@link com.example.demo.model.SpUser}
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto implements Serializable {
    private Long id;
    private String email;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private Boolean status;
    @JsonIgnore
    private String verificationCode;
    @JsonIgnore
    private boolean enabled;
    @JsonIgnore
    private String resetToken;
    @JsonIgnore
    private LocalDateTime tokenExpirationTime;
    private RoleDto role;
    private ProfileDto profile;
    private List<BudgetDto> budgets;
    private List<CategoryDto> categories;


    public UserDto(SpUser user, boolean details) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.status = user.getStatus();
        this.role = user.getRole() != null ? new RoleDto(user.getRole(), false) : null;

        // Only instantiate ProfileDto if details flag is true
        if (details) {
            this.profile = user.getProfile() != null ? new ProfileDto(user.getProfile(), false) : null;
        }

        this.verificationCode = user.getVerificationCode();
        this.enabled = user.isEnabled();
        this.resetToken = user.getResetToken();
        this.tokenExpirationTime = user.getTokenExpirationTime();

        if (details) {
            this.budgets = user.getBudgets() != null ?
                    user.getBudgets().stream().peek(budget -> budget.setUser(null)).map(budget -> new BudgetDto(budget, false)).toList() :
                    new ArrayList<>();
            this.categories = user.getCategories() != null ?
                    user.getCategories().stream().peek(category -> category.setUser(null)).map(category -> new CategoryDto(category, false)).toList() :
                    new ArrayList<>();
        }
    }

}