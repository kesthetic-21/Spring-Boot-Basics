package io.springboot.java.meetingscheduler.dto;

import lombok.Data;

@Data
public class AuthResponseDto {
    private String accessToken;
    private String tokenType = "Bearer ";
    private String redirectUrl;

    public AuthResponseDto(String accessToken) {
        this.accessToken = accessToken;
    }
}