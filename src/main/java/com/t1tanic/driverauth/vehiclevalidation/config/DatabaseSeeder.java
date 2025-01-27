package com.t1tanic.driverauth.vehiclevalidation.config;

import com.t1tanic.driverauth.vehiclevalidation.model.Driver;
import com.t1tanic.driverauth.vehiclevalidation.model.Role;
import com.t1tanic.driverauth.vehiclevalidation.model.User;
import com.t1tanic.driverauth.vehiclevalidation.repository.DriverRepository;
import com.t1tanic.driverauth.vehiclevalidation.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final DriverRepository driverRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseSeeder(DriverRepository driverRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.driverRepository = driverRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (driverRepository.count() == 0) {
            driverRepository.save(Driver.builder().fullName("John Doe").licenseNumber("D123456").isValid(true).build());
            driverRepository.save(Driver.builder().fullName("Jane Smith").licenseNumber("D654321").isValid(false).build());
            driverRepository.save(Driver.builder().fullName("Alice Johnson").licenseNumber("D789012").isValid(true).build());
            driverRepository.save(Driver.builder().fullName("Bob Brown").licenseNumber("D345678").isValid(true).build());
            System.out.println("Database populated with initial drivers.");
        } else {
            System.out.println("Database already contains driver data.");
        }

        if (userRepository.count() == 0) {
            userRepository.save(User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .email("admin@example.com")
                    .role(Role.ROLE_ADMIN)
                    .build());

            userRepository.save(User.builder()
                    .username("user")
                    .password(passwordEncoder.encode("user123"))
                    .email("user@example.com")
                    .role(Role.ROLE_USER)
                    .build());

            System.out.println("Database populated with initial users.");
        } else {
            System.out.println("Database already contains user data.");
        }
    }

}
