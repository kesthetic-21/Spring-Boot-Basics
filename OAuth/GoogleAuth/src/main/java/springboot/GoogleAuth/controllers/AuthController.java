package springboot.GoogleAuth.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springboot.GoogleAuth.services.OAuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private OAuthService oAuthService;

    @GetMapping("/login/oauth2/code/google")
    public String handleGrantCode(@RequestParam("code") String code) {
        try {
            return oAuthService.handleGoogleLogin(code);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

}