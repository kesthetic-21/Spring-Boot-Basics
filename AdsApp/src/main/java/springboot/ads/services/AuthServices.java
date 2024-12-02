package springboot.ads.services;

import springboot.ads.controllers.OTPController;
import springboot.ads.entities.OtpEntity;
import springboot.ads.entities.User;
import springboot.ads.dto.AuthResponseDto;
import springboot.ads.dto.LoginDto;
import springboot.ads.dto.RegisterDto;
import springboot.ads.repository.RoleRepository;
import springboot.ads.repository.UserRepository;
import springboot.ads.entities.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;

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
    private JWTGenerator jwtGenerator;
    @Autowired
    private OTPService otpService;

    public AuthResponseDto login(LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(),
                        loginDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByUsername(loginDto.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + loginDto.getUsername()));

        if (!user.isVerified()) {
            throw new RuntimeException("User is not verified. Please verify your email using the OTP sent.");
        }

        long userId = user.getId();

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
        User user = new User();
        user.setUsername(registerDto.getUsername());
        user.setEmail(registerDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setVerified(false);

        Role roles = roleRepository.findByName("USER").get();
        user.setRoles(Collections.singletonList(roles));

        user.setPreferences(Set.of("food", "coding", "crypto"));

        userRepository.save(user);

        otpService.sendOtpToEmail(registerDto.getEmail());

        return "User Registration Successful! Please verify your registration by the OTP sent!";
    }
}
