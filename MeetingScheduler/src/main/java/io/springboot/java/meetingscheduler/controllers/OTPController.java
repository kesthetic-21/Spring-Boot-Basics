package io.springboot.java.meetingscheduler.controllers;

import io.springboot.java.meetingscheduler.services.OTPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/meetingScheduler")
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

    @GetMapping("/verifyOTP")
    public String showVerifyOTPPage() {
        return "otp-verify"; // Return the HTML template
    }

    @PostMapping("/verifyOTP")
    public String verifyOTP(@RequestParam String otp, @RequestParam String email, Model model) {

        boolean isVerified = otpService.verifyOtp(otp, email);

        if (isVerified) {
            return "redirect:/meetingScheduler/login"; // Redirect to login page
        } else {
            model.addAttribute("error", "Invalid OTP or OTP expired!");
            return "otp-verify"; // Return OTP verification page with error
        }
    }

    @PostMapping("/retryOTP")
    public ResponseEntity<String> retryOTP(@RequestParam String email) {
        return otpService.retryOTP(email);
    }
}