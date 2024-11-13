package com.example.demo.dto;

import com.example.demo.model.SpProfile;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;

/**
 * DTO for {@link com.example.demo.model.SpProfile}
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileDto implements Serializable {
    private Long id;
    private String firstName;
    private String lastName;
    private String profilePic;
    private UserDto user;

    public ProfileDto(SpProfile profile, boolean details) {
        this.id = profile.getId();
        this.firstName = profile.getFirstName();
        this.lastName = profile.getLastName();
        this.profilePic = profile.getProfilePic();

        // Only instantiate UserDto if details flag is true
        if (details) {
            this.user = profile.getUser() != null ? new UserDto(profile.getUser(), false) : null;
        }
    }

}