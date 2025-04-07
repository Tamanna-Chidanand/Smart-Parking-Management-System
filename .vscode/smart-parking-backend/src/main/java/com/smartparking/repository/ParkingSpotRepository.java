package com.smartparking.repository;

import com.smartparking.model.ParkingSpot;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, String> {
    List<ParkingSpot> findByIsAvailableTrue();
    List<ParkingSpot> findByTypeAndIsAvailableTrue(String type);
}