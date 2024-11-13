package com.example.demo.dto;

import com.example.demo.model.SpRole;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO for {@link com.example.demo.model.SpRole}
 */

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoleDto implements Serializable {
    private Long id;
    private String name;
    private String description;
    private List<UserDto> users;

    public RoleDto(SpRole role, boolean details) {
        this.id = role.getId();
        this.name = role.getName();
        this.description = role.getDescription();

        // Only instantiate UserDto if details flag is true
        if (details) {
            this.users = role.getUsers() != null ?
                    role.getUsers().stream().map(user -> new UserDto(user, false)).toList() :
                    new ArrayList<>();
        }
    }

}