package io.springboot.java.meetingscheduler.services;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;

@Service
public class OAuthService {

    @Value("${google.client-id}")
    private String clientId;

    @Value("${google.client-secret}")
    private String clientSecret;

    @Value("${google.redirect-uri}")
    private String redirectUri;

    private final RestTemplate restTemplate = new RestTemplate();

    public String getOauthAccessTokenGoogle(String code) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("redirect_uri", redirectUri);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, httpHeaders);

        String url = "https://oauth2.googleapis.com/token";

        try {
            Map<String, Object> response = restTemplate.postForObject(url, requestEntity, Map.class);
            if (response != null && response.containsKey("access_token")) {
                return response.get("access_token").toString();
            } else {
                throw new RuntimeException("Error fetching access token from Google");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error fetching access token: " + e.getMessage(), e);
        }
    }

    public JsonObject getProfileDetailsGoogle(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        String url = "https://www.googleapis.com/oauth2/v2/userinfo";

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return JsonParser.parseString(response.getBody()).getAsJsonObject();
            } else {
                throw new RuntimeException("Failed to fetch user profile details");
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error fetching Google user profile: " + ex.getMessage(), ex);
        }
    }
}