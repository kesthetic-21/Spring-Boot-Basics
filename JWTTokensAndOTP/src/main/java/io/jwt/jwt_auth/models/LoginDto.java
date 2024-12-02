package io.jwt.jwt_auth.models;

import lombok.Data;

@Data
public class LoginDto {
    private String username;
    private String password;
}
