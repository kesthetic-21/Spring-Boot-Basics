package io.jwt.jwt_auth.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RegisterDto {
    private String username;

    @Email(message = "Email should be valid")
    private String email;
//    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@gmail\\.com$", message = "Email must be a valid Gmail address")

    private String password;
    private String otp;
}