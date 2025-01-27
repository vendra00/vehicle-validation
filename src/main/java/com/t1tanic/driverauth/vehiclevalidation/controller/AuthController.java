package com.t1tanic.driverauth.vehiclevalidation.controller;

import com.t1tanic.driverauth.vehiclevalidation.model.AuthResponse;
import com.t1tanic.driverauth.vehiclevalidation.model.User;
import com.t1tanic.driverauth.vehiclevalidation.security.JwtUtil;
import com.t1tanic.driverauth.vehiclevalidation.service.TokenBlacklistService;
import com.t1tanic.driverauth.vehiclevalidation.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final TokenBlacklistService tokenBlacklistService;

    public AuthController(JwtUtil jwtUtil, UserService userService, TokenBlacklistService tokenBlacklistService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody Map<String, String> credentials) {
        String providedUsername = credentials.get("username");
        String providedPassword = credentials.get("password");

        return userService.getUserByUsername(providedUsername)
                .filter(user -> userService.passwordMatches(providedPassword, user.getPassword()))
                .map(user -> {
                    logger.info("User {} logged in successfully", providedUsername);
                    String token = jwtUtil.generateToken(providedUsername);
                    return ResponseEntity.ok(new AuthResponse(token, "Login successful"));
                })
                .orElseGet(() -> {
                    logger.warn("Invalid login attempt for username: {}", providedUsername);
                    return ResponseEntity.status(401).body(new AuthResponse(null, "Invalid credentials"));
                });
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            long expiration = jwtUtil.getExpiration(token).getTime() - System.currentTimeMillis();
            tokenBlacklistService.blacklistToken(token, expiration / 1000);  // Convert to seconds
            logger.info("Token blacklisted: {}", token);
        }
        return ResponseEntity.ok(Map.of("message", "Logout successful"));
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody User user) {
        try {
            userService.registerUser(user);
            return ResponseEntity.ok(Map.of("message", "User registered successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
