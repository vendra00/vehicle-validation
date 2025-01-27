package com.t1tanic.driverauth.vehiclevalidation.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {
    private String token;
    private String password;
}

