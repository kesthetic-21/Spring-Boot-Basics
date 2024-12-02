package io.springboot.java.meetingscheduler.services;

import io.springboot.java.meetingscheduler.entities.OtpEntity;
import io.springboot.java.meetingscheduler.entities.User;
import io.springboot.java.meetingscheduler.repository.OTPRepository;
import io.springboot.java.meetingscheduler.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class OTPService {

    @Autowired
    private OTPRepository otpRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    public boolean isEmailRegistered(String email) {
        return userRepository.existsByEmail(email);
    }

    public void sendOtpToEmail(String email) {
        String otp = generateOTP();
        saveOtp(otp, email);
        sendEmail(otp, email);
    }

    private String generateOTP() {
        return String.format("%05d", new Random().nextInt(100000));
    }

    private void saveOtp(String otp, String email) {
        OtpEntity otpEntity = otpRepository.findByOTPUserEmail(email).orElse(new OtpEntity());
        otpEntity.setOtp(otp);
        otpEntity.setUser_email(email);
        otpEntity.setOtp_generation_time(LocalDateTime.now());
        otpEntity.setOtp_expiration_time(LocalDateTime.now().plusMinutes(5));
        otpEntity.setIsAlreadyUsed(false);
        otpEntity.setIsVerified(false);
        otpEntity.setRetryCount(0);
        otpRepository.save(otpEntity);
    }

    private void sendEmail(String otp, String email) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Registration OTP Verification");
            message.setText("Your OTP for registration is: " + otp);
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Error sending email: " + e.getMessage());
        }
    }

    public ResponseEntity<String> retryOTP(String email) {
        LocalDateTime currentTime = LocalDateTime.now();
        Optional<OtpEntity> validOtpOptional = otpRepository.findValidOTPByEmailAndCurrentTime(email, currentTime);

        if (validOtpOptional.isPresent()) {
            OtpEntity otpEntity = validOtpOptional.get();

            if (otpEntity.getRetryCount() >= 3) {
                if (otpEntity.getRetryRestrictionTime() == null ||
                        otpEntity.getRetryRestrictionTime().isBefore(currentTime.minusMinutes(1))) {
                    // Reset restriction after 1 minute
                    otpEntity.setRetryCount(0);
                    otpEntity.setRetryRestrictionTime(null);
                    otpEntity.setIsAlreadyUsed(true);
                    otpEntity.setIsVerified(false);
                    otpRepository.save(otpEntity);
                } else {
                    return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                            .body("Retry limit exceeded. Please wait for 1 minute before retrying.");
                }
            }

            return ResponseEntity.ok("Retry allowed. You can request a new OTP!");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No valid OTP found for the provided email.");
    }

    public boolean verifyOtp(String otp, String email) {
        LocalDateTime currentTime = LocalDateTime.now();
        Optional<OtpEntity> validOtpOptional = otpRepository.findValidOTPByEmailAndCurrentTime(email, currentTime);

        if (validOtpOptional.isPresent()) {
            OtpEntity otpEntity = validOtpOptional.get();

            if (otpEntity.getRetryCount() >= 3) {
                if (otpEntity.getRetryRestrictionTime() != null &&
                        otpEntity.getRetryRestrictionTime().isAfter(currentTime.minusMinutes(1))) {
                    return false; // User is still restricted
                }
            }

            if (otpEntity.getOtp().equals(otp)) {
                otpEntity.setIsVerified(true);
                otpEntity.setIsAlreadyUsed(true);
                otpRepository.save(otpEntity);

                User user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
                user.setVerified(true);
                userRepository.save(user);

                return true; // OTP verified successfully
            } else {
                otpEntity.setRetryCount(otpEntity.getRetryCount() + 1);
                if (otpEntity.getRetryCount() >= 3) {
                    otpEntity.setRetryRestrictionTime(LocalDateTime.now());
                }
                otpRepository.save(otpEntity);
            }
        }

        return false;
    }
}