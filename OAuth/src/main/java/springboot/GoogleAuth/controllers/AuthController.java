package springboot.GoogleAuth.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springboot.GoogleAuth.services.OAuthService;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private OAuthService oAuthService;

    @GetMapping("/login/oauth2/code/google")
    public Map<String, Object> handleGrantCode(@RequestParam("code") String code) {
        return oAuthService.handleGoogleLogin(code);
    }

    @GetMapping("/login/oauth2/code/facebook")
    public Map<String, Object> handleFbCode(@RequestParam("code") String code) {
        return oAuthService.handleFacebookLogin(code);
    }

    @GetMapping("/data-deletion")
    public ResponseEntity<String> deleteData() {
        return ResponseEntity.ok("No user data is stored in this app. For concerns, contact email@example.com.");
    }
}