package com.t1tanic.driverauth.vehiclevalidation.service;

import com.t1tanic.driverauth.vehiclevalidation.model.User;
import com.t1tanic.driverauth.vehiclevalidation.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Override
    public User registerUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists.");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already registered.");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found.");
        }
        userRepository.deleteById(id);
    }

    @Override
    public boolean passwordMatches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @Override
    public void resetPassword(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String token = UUID.randomUUID().toString();

            // Store the token (you can use Redis, database, or in-memory cache)
            user.setResetToken(token);
            userRepository.save(user);

            String resetLink = "http://TODO-your-frontend-app.com/reset-password?token=" + token;

            emailService.sendEmail(email, "Password Reset Request",
                    "<p>Click the link below to reset your password:</p>"
                            + "<a href=\"" + resetLink + "\">Reset Password</a>");

            System.out.println("Password reset email sent to: " + email);
        } else {
            throw new RuntimeException("Email not found");
        }
    }

    @Override
    public Optional<User> getUserByResetToken(String token) {
        return userRepository.findByResetToken(token);
    }


}
