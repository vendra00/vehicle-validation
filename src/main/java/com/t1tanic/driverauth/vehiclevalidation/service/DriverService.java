package com.t1tanic.driverauth.vehiclevalidation.service;

import com.t1tanic.driverauth.vehiclevalidation.model.Driver;

import java.util.List;


public interface DriverService {
    Driver validateLicense(String licenseNumber);
    Driver updateDriver(Long id, Driver driver);
    Driver addDriver(Driver driver);
    List<Driver> getAllDrivers();
    void deleteDriver(Long id);
}
