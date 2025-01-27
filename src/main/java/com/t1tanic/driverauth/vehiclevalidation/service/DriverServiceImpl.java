package com.t1tanic.driverauth.vehiclevalidation.service;

import com.t1tanic.driverauth.vehiclevalidation.exception.DriverNotFoundException;
import com.t1tanic.driverauth.vehiclevalidation.model.Driver;
import com.t1tanic.driverauth.vehiclevalidation.repository.DriverRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;

    public DriverServiceImpl(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    @Override
    public Driver validateLicense(String licenseNumber) {
        log.info("Validating driver with license number: {}", licenseNumber);
        Driver driver = driverRepository.findByLicenseNumber(licenseNumber);
        if (driver != null && driver.isValid()) {
            return driver;
        }
        return null;
    }

    @Override
    public Driver updateDriver(Long id, Driver driver) {
        log.info("Updating driver with id: {}", id);
        return driverRepository.findById(id)
                .map(existingDriver -> {
                    existingDriver.setFullName(driver.getFullName());
                    existingDriver.setLicenseNumber(driver.getLicenseNumber());
                    existingDriver.setValid(driver.isValid());
                    return driverRepository.save(existingDriver);
                })
                .orElseThrow(() -> new DriverNotFoundException("Driver not found with id: " + id));
    }

    @Override
    public Driver addDriver(Driver driver) {
        log.info("Adding new driver: {}", driver.getFullName());
        return driverRepository.save(driver);
    }

    @Override
    public List<Driver> getAllDrivers() {
        log.info("Fetching all drivers");
        return driverRepository.findAll();
    }

    @Override
    public void deleteDriver(Long id) {
        log.info("Deleting driver with id: {}", id);
        driverRepository.deleteById(id);
    }
}
