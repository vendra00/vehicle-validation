package com.t1tanic.driverauth.vehiclevalidation.utils;

public class PasswordEncrypter {
    public static void main(String[] args) {
        org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder =
                new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
        System.out.println(encoder.encode("password"));  // Copy this to your properties
    }
}
