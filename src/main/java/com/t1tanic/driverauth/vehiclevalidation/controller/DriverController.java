package com.t1tanic.driverauth.vehiclevalidation.controller;

import com.t1tanic.driverauth.vehiclevalidation.model.Driver;
import com.t1tanic.driverauth.vehiclevalidation.service.DriverService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/driver")
public class DriverController {

    @Autowired
    private DriverService driverService;

    @GetMapping("/validate/{licenseNumber}")
    public String validateDriver(@PathVariable String licenseNumber) {
        Driver driver = driverService.validateLicense(licenseNumber);
        if (driver != null) {
            return "Driver is valid: " + driver.getFullName();
        }
        return "Invalid driver!";
    }

    @GetMapping("/all")
    public ResponseEntity<List<Driver>> getAllDrivers() {
        return ResponseEntity.ok(driverService.getAllDrivers());
    }

    @PostMapping
    public ResponseEntity<Driver> addDriver(@Valid @RequestBody Driver driver) {
        return ResponseEntity.ok(driverService.addDriver(driver));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Driver> updateDriver(@PathVariable Long id, @Valid @RequestBody Driver driver) {
        return ResponseEntity.ok(driverService.updateDriver(id, driver));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDriver(@PathVariable Long id) {
        driverService.deleteDriver(id);
        return ResponseEntity.ok("Driver deleted successfully");
    }
}
