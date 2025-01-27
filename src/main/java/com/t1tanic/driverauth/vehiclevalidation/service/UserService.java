package com.t1tanic.driverauth.vehiclevalidation.service;

import com.t1tanic.driverauth.vehiclevalidation.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User registerUser(User user);
    void saveUser(User user);
    Optional<User> getUserById(Long id);
    Optional<User> getUserByUsername(String username);
    Optional<User> getUserByEmail(String email);
    List<User> getAllUsers();
    void deleteUser(Long id);
    boolean passwordMatches(String rawPassword, String encodedPassword);
}
