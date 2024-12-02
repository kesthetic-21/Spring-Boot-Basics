package io.springboot.java.meetingscheduler.controllers;

import com.google.gson.JsonObject;
import io.springboot.java.meetingscheduler.dto.AuthResponseDto;
import io.springboot.java.meetingscheduler.dto.LoginDTO;
import io.springboot.java.meetingscheduler.dto.RegisterDTO;
import io.springboot.java.meetingscheduler.repository.UserRepository;
import io.springboot.java.meetingscheduler.services.AuthService;
import io.springboot.java.meetingscheduler.services.OAuthService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/meetingScheduler")
public class AuthController {

    @Autowired
    private AuthService authService;

    private final OAuthService oAuthService;
    @Autowired
    private UserRepository userRepository;

    public AuthController(OAuthService oAuthService) {
        this.oAuthService = oAuthService;
    }

    //Login Page
    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("pageTitle", "Login - Meeting Scheduler");
        model.addAttribute("loginDTO", new LoginDTO());
        return "login"; // Refers to `login.html`
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> loginUser(@ModelAttribute LoginDTO loginDTO, Model model, HttpServletResponse httpResponse) throws Exception {

        AuthResponseDto response = authService.login(loginDTO, model, httpResponse);

        String userRole = authService.getUserRole(loginDTO);
        model.addAttribute("userRole", userRole);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/login/oauth2/code/google")
    public String handleGrantCode(@RequestParam("code") String code) {
        try {
            String accessToken = oAuthService.getOauthAccessTokenGoogle(code);
            JsonObject userDetails = oAuthService.getProfileDetailsGoogle(accessToken);

            String email = userDetails.get("email").getAsString();

            if(userRepository.existsByEmail(email)) {

                String role = userRepository.findByEmail(email).get().getRole().name();

                if ("MENTEE".equals(role)) {
                    return "redirect:/meetingScheduler/mentee/dashboard";
                } else if ("MENTOR".equals(role)) {
                    return "redirect:/meetingScheduler/mentor/dashboard";
                }
            }

            return "redirect:/meetingScheduler/register?email=" + email;

        } catch (Exception e) {
            return "redirect:/meetingScheduler/login?error";
        }
    }

    //Register Page
    @GetMapping("/register")
    public String registerPage(@RequestParam("email") String email, Model model) {
        model.addAttribute("pageTitle", "Register - Meeting Scheduler");
        model.addAttribute("registerDTO", new RegisterDTO());
        model.addAttribute("email", email);
        return "register"; // Refers to `register.html`
    }



    @PostMapping("/register")
    public String registerUser(@ModelAttribute RegisterDTO registerDTO, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            authService.register(registerDTO);
            model.addAttribute("success", "Registration successful! Please check your email for the OTP.");
            return "redirect:/meetingScheduler/verifyOTP"; // Redirect to OTP verification page
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }
}
