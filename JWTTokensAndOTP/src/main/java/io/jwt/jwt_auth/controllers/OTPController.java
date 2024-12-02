package io.jwt.jwt_auth.controllers;

import io.jwt.jwt_auth.services.OTPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class OTPController {

    @Autowired
    private OTPService otpService;

    @PostMapping("/sendOTP")
    public ResponseEntity<String> sendOTP(@RequestParam String email) {
        if (!otpService.isEmailRegistered(email)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email Not Found");
        }
        otpService.sendOtpToEmail(email);
        return ResponseEntity.ok("OTP sent!");
    }

    @PostMapping("/verifyOTP")
    public ResponseEntity<String> verifyOTP(@RequestParam String otp, @RequestParam String email) {
        ResponseEntity<String> retryResponse = otpService.retryOTP(email);
        if (retryResponse.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
            return retryResponse; // User is restricted
        }

        boolean isVerified = otpService.verifyOtp(otp, email);
        if (isVerified) {
            return ResponseEntity.ok("OTP verified!");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP or OTP expired!");
        }
    }

    @PostMapping("/retryOTP")
    public ResponseEntity<String> retryOTP(@RequestParam String email) {
        return otpService.retryOTP(email);
    }
}