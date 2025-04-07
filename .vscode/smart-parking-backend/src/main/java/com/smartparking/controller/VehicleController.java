package com.smartparking.controller;

import com.smartparking.model.Vehicle;
import com.smartparking.service.VehicleService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {
    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @PostMapping
    public Vehicle registerVehicle(@RequestBody Vehicle vehicle) {
        return vehicleService.registerVehicle(vehicle);
    }

    @GetMapping("/{licensePlate}")
    public Vehicle getVehicle(@PathVariable String licensePlate) {
        return vehicleService.getVehicleByLicense(licensePlate);
    }
}