package io.springboot.java.meetingscheduler.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "otp", indexes = {@Index(name = "idx_email", columnList = "user_email")})
@Data
@NoArgsConstructor
public class OtpEntity {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "OTP cannot be NULL")
    private String otp;

    @NotNull(message = "User Email cannot be NULL")
    private String user_email;

    @NotNull(message = "OTP Generation Time cannot be NULL")
    private LocalDateTime otp_generation_time;

    @NotNull(message = "OTP Expiry Time cannot be NULL")
    private LocalDateTime otp_expiration_time;

    private Boolean isVerified = false;

    private Boolean isAlreadyUsed = false;

    private int retryCount = 0;

    private LocalDateTime retryRestrictionTime;
}