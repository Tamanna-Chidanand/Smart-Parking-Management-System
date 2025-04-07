package com.smartparking.controller;

import com.smartparking.model.ParkingSpot;
import com.smartparking.service.ParkingService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/parking")
public class ParkingController {
    private final ParkingService parkingService;

    public ParkingController(ParkingService parkingService) {
        this.parkingService = parkingService;
    }

    @GetMapping("/spots")
    public List<ParkingSpot> getAllSpots() {
        return parkingService.getAllSpots();
    }

    @GetMapping("/spots/available")
    public List<ParkingSpot> getAvailableSpots() {
        return parkingService.getAvailableSpots();
    }

    @GetMapping("/spots/available/{type}")
    public List<ParkingSpot> getAvailableSpotsByType(@PathVariable String type) {
        return parkingService.getAvailableSpotsByType(type);
    }

    @PutMapping("/spots/{spotId}/availability")
    public ParkingSpot updateSpotAvailability(@PathVariable String spotId, @RequestParam boolean available) {
        return parkingService.updateSpotAvailability(spotId, available);
    }
}