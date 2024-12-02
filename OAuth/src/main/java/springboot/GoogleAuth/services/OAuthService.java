package springboot.GoogleAuth.services;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import springboot.GoogleAuth.entities.User;
import springboot.GoogleAuth.repository.UserRepository;
import reactor.core.publisher.Mono;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Service
public class OAuthService {

    private static final Logger logger = LoggerFactory.getLogger(OAuthService.class);

    @Value("${google.client-id}")
    private String clientId;

    @Value("${google.client-secret}")
    private String clientSecret;

    @Value("${google.redirect-uri}")
    private String redirectUri;

    @Value("${facebook.client-id}")
    private String fbClient;

    @Value("${facebook.client-secret}")
    private String fbSecret;

    @Value("${facebook.redirect-uri}")
    private String fbRedirect;

    @Value("${facebook.scope}")
    private String fbScope;

    @Value("${google.token-url}")
    private String googleTokenUrl;

    @Value("${facebook.token-url}")
    private String facebookTokenUrl;

    @Value("${google.user-details}")
    private String googleUserDetails;

    @Value("${facebook.user-details}")
    private String facebookUserDetails;

    @Autowired
    private UserRepository userRepository;

    private final WebClient webClient = WebClient.builder().baseUrl("https://www.googleapis.com").build();

    private Mono<Map<String, Object>> getAccessToken(String url, MultiValueMap<String, String> params) {
        return webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(params)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .doOnError(e -> logger.error("Error fetching access token"));
    }
    
    private Mono<JsonObject> getUserDetails(String url, String accessToken) {
        return webClient.get()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> JsonParser.parseString(response).getAsJsonObject())
                .doOnError(e -> logger.error("Error fetching user profile"));
    }
    
    private Map<String, Object> handleLogin(String accessToken, String platform) {
        JsonObject userDetails = getUserDetails(platform.equals("google") ? googleUserDetails : facebookUserDetails, accessToken)
                .block();

        String platformId = userDetails.get("id").getAsString();
        String email = userDetails.get("email").getAsString();
        String name = userDetails.get("name").getAsString();

        Map<String, Object> response = new HashMap<>();

        if (userRepository.existsByEmail(email)) {
            response.put("status", "success");
            response.put("message", "Logged in successfully.");
            response.put("email", email);
            return response;
        }

        User user = platform.equals("google") ?
                userRepository.findByGoogleId(platformId).orElseGet(() -> createUser(platformId, email, name, platform)) :
                userRepository.findByFacebookId(platformId).orElseGet(() -> createUser(platformId, email, name, platform));

        response.put("status", "success");
        response.put("message", "Registered successfully. You can now proceed to login!");
        response.put("email", email);

        return response;
    }

    private User createUser(String platformId, String email, String name, String platform) {
        User newUser = new User();
        if (platform.equals("google")) {
            newUser.setGoogleId(platformId);
        } else {
            newUser.setFacebookId(platformId);
        }
        newUser.setEmail(email);
        newUser.setName(name);
        return userRepository.save(newUser);
    }

    public Map<String, Object> handleGoogleLogin(String code) {
        return handleOAuthLogin(code, "google");
    }

    public Map<String, Object> handleFacebookLogin(String code) {
        return handleOAuthLogin(code, "facebook");
    }

    private Map<String, Object> handleOAuthLogin(String code, String platform) {
        String accessToken = getAccessTokenFromCode(code, platform);

        Map<String, Object> response = new HashMap<>();
        response.put("code", code);
        response.put("platform", platform);

        return response;
    }

    private String getAccessTokenFromCode(String code, String platform) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        if (platform.equals("google")) {
            params.add("code", code);
            params.add("redirect_uri", redirectUri);
            params.add("client_id", clientId);
            params.add("client_secret", clientSecret);
            params.add("grant_type", "authorization_code");

            return getAccessToken(googleTokenUrl, params)
                    .map(response -> response.get("access_token").toString())
                    .block();

        } else {
            params.add("code", code);
            params.add("client_id", fbClient);
            params.add("client_secret", fbSecret);
            params.add("redirect_uri", fbRedirect);
            params.add("scope", fbScope);

            return getAccessToken(facebookTokenUrl, params)
                    .map(response -> response.get("access_token").toString())
                    .block();
        }
    }
}