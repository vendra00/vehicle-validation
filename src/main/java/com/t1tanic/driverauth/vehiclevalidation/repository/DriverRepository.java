package com.t1tanic.driverauth.vehiclevalidation.repository;


import com.t1tanic.driverauth.vehiclevalidation.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverRepository extends JpaRepository<Driver, Long> {
    Driver findByLicenseNumber(String licenseNumber);
}
