package com.orderplatform.userservice.auth.controller;


import com.orderplatform.common.dto.ApiResponse;
import com.orderplatform.userservice.auth.service.AuthService;
import com.orderplatform.userservice.auth.dto.response.AuthResponse;
import com.orderplatform.userservice.auth.dto.request.ForgotPasswordRequest;
import com.orderplatform.userservice.auth.dto.request.LoginRequest;
import com.orderplatform.userservice.auth.dto.request.RefreshTokenRequest;
import com.orderplatform.userservice.auth.dto.request.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok(authService.login(request));
    }

    // TODO: Implement refresh, forgot-password and OAuth2 flows.
    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public ApiResponse<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return null;
    }

    @PostMapping("/forgot-password")
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public ApiResponse<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        return null;
    }

    // provider: google | apple
    @GetMapping("/oauth2/{provider}")
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public void oauth2(@PathVariable String provider) {
    }

}
