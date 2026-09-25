package com.andesstay.controller;

import com.andesstay.dto.AuthTokenResponse;
import com.andesstay.dto.CognitoCallbackRequest;
import com.andesstay.dto.CognitoTokenResponse;
import com.andesstay.service.CognitoAuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final CognitoAuthService cognitoAuthService;

    public AuthController(CognitoAuthService cognitoAuthService) {
        this.cognitoAuthService = cognitoAuthService;
    }

    @PostMapping("/cognito/callback")
    public AuthTokenResponse cognitoCallback(@Valid @RequestBody CognitoCallbackRequest request) {
        CognitoTokenResponse tokens = cognitoAuthService.exchangeCodeForTokens(request.getCode(), request.getRedirectUri());
        return new AuthTokenResponse(
                tokens.getIdToken(), tokens.getAccessToken(), tokens.getRefreshToken(),
                tokens.getExpiresIn(), tokens.getTokenType()
        );
    }
}
