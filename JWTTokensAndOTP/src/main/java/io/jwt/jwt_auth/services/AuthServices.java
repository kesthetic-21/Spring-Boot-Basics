package io.jwt.jwt_auth.services;

import io.jwt.jwt_auth.controllers.OTPController;
import io.jwt.jwt_auth.entities.OtpEntity;
import io.jwt.jwt_auth.entities.UserEntity;
import io.jwt.jwt_auth.models.AuthResponseDto;
import io.jwt.jwt_auth.models.LoginDto;
import io.jwt.jwt_auth.models.RegisterDto;
import io.jwt.jwt_auth.repository.RoleRepository;
import io.jwt.jwt_auth.repository.UserRepository;
import io.jwt.jwt_auth.entities.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class AuthServices {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtGenerator jwtGenerator;
    @Autowired
    private OTPService otpService;

    public AuthResponseDto login(LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(),
                        loginDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserEntity user = userRepository.findByUsername(loginDto.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + loginDto.getUsername()));

        if (!user.isVerified()) {
            throw new RuntimeException("User is not verified. Please verify your email using the OTP sent.");
        }

        int userId = user.getId();

        String token = jwtGenerator.generateToken(authentication, userId);
        return new AuthResponseDto(token);
    }

    public String register(RegisterDto registerDto) {
        // Checking if the username passed already exists or not
        if (userRepository.existsByUsername(registerDto.getUsername())) {
            return "Username is already taken!";
        }

        //Checking if email passed already exists or not
        if(userRepository.existsByEmail(registerDto.getEmail())) {
            return "Email is already taken!";
        }

        // Creating a User with the credentials passed in the Register form
        UserEntity user = new UserEntity();
        user.setUsername(registerDto.getUsername());
        user.setEmail(registerDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setVerified(false);

        Role roles = roleRepository.findByName("USER").get();
        user.setRoles(Collections.singletonList(roles));

        userRepository.save(user);

        otpService.sendOtpToEmail(registerDto.getEmail());

        return "User Registration Successful! Please verify your registration by the OTP sent!";
    }
}
