package io.springboot.java.meetingscheduler.dto;

import io.springboot.java.meetingscheduler.enums.UserRoles;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class LoginDTO {

    @NotEmpty(message = "Username is required")
    private String username;

    @NotEmpty(message = "Password is required")
    private String password;

    private UserRoles role;
}