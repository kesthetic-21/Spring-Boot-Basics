package io.springboot.java.meetingscheduler.services;

import io.springboot.java.meetingscheduler.dto.AuthResponseDto;
import io.springboot.java.meetingscheduler.dto.LoginDTO;
import io.springboot.java.meetingscheduler.dto.RegisterDTO;
import io.springboot.java.meetingscheduler.entities.User;
import io.springboot.java.meetingscheduler.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JWTGenerator jwtGenerator;
    @Autowired
    private OTPService otpService;
    @Autowired
    private HttpServletRequest request;

    public AuthResponseDto login(LoginDTO loginDTO, Model model, HttpServletResponse response) throws Exception {

        System.out.println("Authenticating user: " + loginDTO.getUsername());

        try {
            // Authenticate the user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDTO.getUsername(),
                            loginDTO.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Retrieve user details
            User user = userRepository.findByUsername(loginDTO.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + loginDTO.getUsername()));


            //Storing the user in session
            HttpSession session = request.getSession();
            session.setAttribute("loggedInUser", user);

            System.out.println("Encoded password: " + user.getPassword());
            System.out.println("Password entered by user: " + loginDTO.getPassword());

            // Check if user is verified
            if (!user.isVerified()) {
                throw new RuntimeException("User is not verified. Please verify your email using the OTP sent.");
            }

            // Generate JWT
            int userId = user.getId();
            String token = jwtGenerator.generateToken(authentication, userId);

            // Determine the user's role
            String role = user.getRole().name();
            String redirectUrl;

            switch (role) {
                case "MENTEE" -> redirectUrl = "/meetingScheduler/mentee/dashboard";
                case "MENTOR" -> redirectUrl = "/meetingScheduler/mentor/dashboard";
                default -> throw new RuntimeException("User role not recognized.");
            }

            System.out.println("Redirecting user with role " + role + " to: " + redirectUrl);

            // Redirect the user to their dashboard
            response.sendRedirect(redirectUrl);

            // Optionally, you can still return the token and redirectUrl if needed
            AuthResponseDto authResponse = new AuthResponseDto(token);
            authResponse.setRedirectUrl(redirectUrl);
            return authResponse;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Login failed: " + e.getMessage());
        }
    }

    public void register(RegisterDTO registerDTO){

        // Checking if the username passed already exists or not
        if (userRepository.existsByUsername(registerDTO.getUsername())) {
            System.out.println("Username is already taken!");
        }

        //Checking if email passed already exists or not
        if(userRepository.existsByEmail(registerDTO.getEmail())) {
            System.out.println("Email is already taken!");
        }

        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setEmail(registerDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setRole(registerDTO.getRole());
        user.setVerified(false);

        userRepository.save(user);

        otpService.sendOtpToEmail(registerDTO.getEmail());
    }

    public String getUserRole(LoginDTO loginDTO){

        User user = userRepository.findByUsername(loginDTO.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + loginDTO.getUsername()));

        String role = user.getRole().name();

        return role;
    }
}
