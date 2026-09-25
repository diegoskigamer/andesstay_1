package com.andesstay.service;

import com.andesstay.dto.CognitoTokenResponse;
import com.andesstay.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class CognitoAuthService {

    private final RestTemplate restTemplate = new RestTemplate();

    // Valores por defecto fijos para garantizar que nunca vuelva a usar "dominio-appejemplo"
    @Value("${cognito.domain:andesstay-app.auth.us-east-1.amazoncognito.com}")
    private String cognitoDomain;

    @Value("${cognito.client-id:28s01tgal6ti9qtv4vnroidreo}")
    private String clientId;

    @Value("${cognito.client-secret:}")
    private String clientSecret;

    public CognitoTokenResponse exchangeCodeForTokens(String code, String redirectUri) {
        // Validación de respaldo asegurando que tome el dominio fijo si viniera vacío
        if (cognitoDomain == null || cognitoDomain.isBlank() || cognitoDomain.contains("dominio-appejemplo")) {
            cognitoDomain = "andesstay-app.auth.us-east-1.amazoncognito.com";
        }
        if (clientId == null || clientId.isBlank()) {
            clientId = "28s01tgal6ti9qtv4vnroidreo";
        }

        String tokenUrl = "https://" + cognitoDomain + "/oauth2/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        if (clientSecret != null && !clientSecret.isBlank()) {
            headers.setBasicAuth(basicAuthHeader());
        }

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("code", code);
        body.add("redirect_uri", redirectUri);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            var response = restTemplate.postForEntity(tokenUrl, request, CognitoTokenResponse.class);
            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                throw new BusinessException("Cognito no devolvió tokens válidos.");
            }
            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new BusinessException(
                    "Cognito rechazó el intercambio de código: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }
    }

    private String basicAuthHeader() {
        String raw = clientId + ":" + clientSecret;
        return Base64.getEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }
}