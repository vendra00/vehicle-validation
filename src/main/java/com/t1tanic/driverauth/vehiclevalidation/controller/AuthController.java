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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final TokenBlacklistService tokenBlacklistService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(JwtUtil jwtUtil, UserService userService, TokenBlacklistService tokenBlacklistService, PasswordEncoder passwordEncoder) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.tokenBlacklistService = tokenBlacklistService;
        this.passwordEncoder = passwordEncoder;
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

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody User user) {
        try {
            userService.registerUser(user);
            return ResponseEntity.ok(Map.of("message", "User registered successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/update-profile/{id}")
    public ResponseEntity<Map<String, String>> updateProfile(@PathVariable Long id, @RequestBody User updatedUser) {
        Optional<User> existingUser = userService.getUserById(id);
        if (existingUser.isPresent()) {
            User userToUpdate = existingUser.get();
            userToUpdate.setUsername(updatedUser.getUsername());
            userToUpdate.setEmail(updatedUser.getEmail());
            if (!updatedUser.getPassword().isEmpty()) {
                userToUpdate.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            }
            userService.registerUser(userToUpdate);
            return ResponseEntity.ok(Map.of("message", "User profile updated successfully"));
        } else {
            return ResponseEntity.status(404).body(Map.of("message", "User not found"));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestParam String email) {
        Optional<User> user = userService.getUserByEmail(email);
        if (user.isPresent()) {
            String resetToken = jwtUtil.generateToken(email);
            // In real case, send email with token link (omitted for brevity)
            logger.info("Password reset token generated for {}", email);
            return ResponseEntity.ok(Map.of("message", "Password reset link sent", "token", resetToken));
        } else {
            return ResponseEntity.status(404).body(Map.of("message", "Email not found"));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestParam String email, @RequestBody Map<String, String> request) {
        Optional<User> userOptional = userService.getUserByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String newPassword = request.get("newPassword");

            // Update the password field only and save
            user.setPassword(passwordEncoder.encode(newPassword));
            userService.saveUser(user);

            return ResponseEntity.ok(Map.of("message", "Password has been reset successfully"));
        } else {
            return ResponseEntity.status(404).body(Map.of("message", "User not found"));
        }
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
}
