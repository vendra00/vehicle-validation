package com.t1tanic.driverauth.vehiclevalidation.service;

public interface EmailService {
    void sendEmail(String to, String subject, String body);
}
