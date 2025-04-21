package com.smartparking.controller;

import com.smartparking.model.ParkingSpot;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ParkingSpotController {
    
    public ParkingSpotController() {
        initializeParkingSpots();
    }
    
    private void initializeParkingSpots() {
        // First, delete all existing spots to ensure a clean slate
        List<ParkingSpot> existingSpots = ParkingSpot.readAll();
        for (ParkingSpot spot : existingSpots) {
            ParkingSpot.delete(spot.getId());
        }

        // Create a 4x4 grid of parking spots (16 total)
        // Row A - Standard spots (4)
        createSpot("A1", "STANDARD");
        createSpot("A2", "STANDARD");
        createSpot("A3", "STANDARD");
        createSpot("A4", "STANDARD");
        
        // Row B - Standard spots (4)
        createSpot("B1", "STANDARD");
        createSpot("B2", "STANDARD");
        createSpot("B3", "STANDARD");
        createSpot("B4", "STANDARD");
        
        // Row C - Electric spots (4)
        createSpot("C1", "ELECTRIC");
        createSpot("C2", "ELECTRIC");
        createSpot("C3", "ELECTRIC");
        createSpot("C4", "ELECTRIC");
        
        // Row D - Handicapped spots (3) and Electric spot (1)
        createSpot("D1", "HANDICAPPED");
        createSpot("D2", "HANDICAPPED");
        createSpot("D3", "HANDICAPPED");
        createSpot("D4", "ELECTRIC");
    }
    
    public ParkingSpot findAvailableSpot(String type) {
        List<ParkingSpot> allSpots = ParkingSpot.readAll();
        
        // Filter spots by type and availability
        List<ParkingSpot> availableSpots = allSpots.stream()
            .filter(spot -> spot.getType().equalsIgnoreCase(type) && spot.isAvailable())
            .collect(Collectors.toList());
        
        // Return the first available spot or null if none found
        return availableSpots.isEmpty() ? null : availableSpots.get(0);
    }
    
    public List<ParkingSpot> getAllSpots() {
        return ParkingSpot.readAll();
    }
    
    public boolean isSpotAvailable(Long spotId, LocalDateTime endTime) {
        ParkingSpot spot = ParkingSpot.read(spotId);
        if (spot == null || !spot.isAvailable()) {
            return false;
        }
        return true;
    }
    
    public void markSpotAsOccupied(Long spotId) {
        ParkingSpot spot = ParkingSpot.read(spotId);
        if (spot != null) {
            spot.setAvailable(false);
            ParkingSpot.update(spot);
        }
    }
    
    public void markSpotAsAvailable(Long spotId) {
        ParkingSpot spot = ParkingSpot.read(spotId);
        if (spot != null) {
            spot.setAvailable(true);
            ParkingSpot.update(spot);
        }
    }
    
    public ParkingSpot createSpot(String spotNumber, String type) {
        ParkingSpot spot = new ParkingSpot(spotNumber, type);
        return ParkingSpot.create(spot);
    }
} 