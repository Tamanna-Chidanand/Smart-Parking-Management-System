package com.smartparking.service;

import com.smartparking.model.ParkingSpot;
import com.smartparking.repository.ParkingSpotRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ParkingService {
    private final ParkingSpotRepository parkingSpotRepository;

    public ParkingService(ParkingSpotRepository parkingSpotRepository) {
        this.parkingSpotRepository = parkingSpotRepository;
    }

    public List<ParkingSpot> getAllSpots() {
        return parkingSpotRepository.findAll();
    }

    public List<ParkingSpot> getAvailableSpots() {
        return parkingSpotRepository.findByIsAvailableTrue();
    }

    public List<ParkingSpot> getAvailableSpotsByType(String type) {
        return parkingSpotRepository.findByTypeAndIsAvailableTrue(type);
    }

    public ParkingSpot updateSpotAvailability(String spotId, boolean isAvailable) {
        ParkingSpot spot = parkingSpotRepository.findById(spotId).orElse(null);
        if (spot != null) {
            spot.setAvailable(isAvailable);
            return parkingSpotRepository.save(spot);
        }
        return null;
    }
}