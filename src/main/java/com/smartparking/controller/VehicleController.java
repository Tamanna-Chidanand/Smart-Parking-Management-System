package com.smartparking.controller;

import com.smartparking.model.Vehicle;
import com.smartparking.model.User;
import java.util.List;
import java.util.stream.Collectors;

public class VehicleController {
    
    public Vehicle createVehicle(String licensePlate, String make, String model, Long userId) {
        // Validate if user exists
        User user = User.read(userId);
        if (user == null) {
            throw new IllegalArgumentException("User with ID " + userId + " does not exist.");
        }
        
        // Create and return the vehicle
        Vehicle vehicle = new Vehicle(licensePlate, make, model, userId);
        return Vehicle.create(vehicle);
    }
    
    public Vehicle getVehicle(Long id) {
        return Vehicle.read(id);
    }
    
    public List<Vehicle> getAllVehicles() {
        return Vehicle.readAll();
    }
    
    public List<Vehicle> getVehiclesByUser(Long userId) {
        List<Vehicle> allVehicles = Vehicle.readAll();
        return allVehicles.stream()
            .filter(vehicle -> vehicle.getUserId().equals(userId))
            .collect(Collectors.toList());
    }
    
    public Vehicle updateVehicle(Vehicle vehicle) {
        // Validate if user exists
        User user = User.read(vehicle.getUserId());
        if (user == null) {
            throw new IllegalArgumentException("User with ID " + vehicle.getUserId() + " does not exist.");
        }
        
        return Vehicle.update(vehicle);
    }
    
    public void deleteVehicle(Long id) {
        Vehicle.delete(id);
    }
} 