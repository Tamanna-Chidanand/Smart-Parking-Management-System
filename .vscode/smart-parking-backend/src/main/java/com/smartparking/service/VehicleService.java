package com.smartparking.service;

import com.smartparking.model.Vehicle;
import com.smartparking.repository.VehicleRepository;
import org.springframework.stereotype.Service;

@Service
public class VehicleService {
    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public Vehicle registerVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    public Vehicle getVehicleByLicense(String licensePlate) {
        return vehicleRepository.findById(licensePlate).orElse(null);
    }
}