package com.smartparking.controller;

import com.smartparking.model.Reservation;
import com.smartparking.model.ParkingSpot;
import com.smartparking.model.Vehicle;
import com.smartparking.model.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ReservationController {
    
    private final ParkingSpotController parkingSpotController;
    
    public ReservationController() {
        this.parkingSpotController = new ParkingSpotController();
    }
    
    public Reservation createReservation(Long spotId, Long userId, Long vehicleId, LocalDateTime startTime, LocalDateTime endTime) {
        // Validate if user exists
        User user = User.read(userId);
        if (user == null) {
            throw new IllegalArgumentException("User with ID " + userId + " does not exist.");
        }
        
        // Validate if vehicle exists and belongs to the user
        Vehicle vehicle = Vehicle.read(vehicleId);
        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle with ID " + vehicleId + " does not exist.");
        }
        if (!vehicle.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Vehicle with ID " + vehicleId + " does not belong to user with ID " + userId);
        }
        
        // Check if spot is available for the requested time period
        if (!parkingSpotController.isSpotAvailable(spotId, endTime)) {
            return null;
        }
        
        // Create the reservation using the parameterized constructor
        Reservation reservation = new Reservation(spotId, userId, vehicleId, startTime, endTime);
        
        // Try to create the reservation first
        Reservation createdReservation = Reservation.create(reservation);
        
        // Only mark the spot as occupied if reservation was created successfully
        if (createdReservation != null) {
            parkingSpotController.markSpotAsOccupied(spotId);
        }
        
        return createdReservation;
    }
    
    public Reservation createReservation(Reservation reservation) {
        // Check if spot is available for the requested time period
        if (!parkingSpotController.isSpotAvailable(reservation.getSpotId(), reservation.getEndTime())) {
            return null;
        }
        
        // Try to create the reservation first
        Reservation createdReservation = Reservation.create(reservation);
        
        // Only mark the spot as occupied if reservation was created successfully
        if (createdReservation != null) {
            parkingSpotController.markSpotAsOccupied(reservation.getSpotId());
        }
        
        return createdReservation;
    }
    
    public boolean cancelReservation(Long reservationId) {
        Reservation reservation = Reservation.read(reservationId);
        if (reservation == null || !"ACTIVE".equals(reservation.getStatus())) {
            return false;
        }
        
        // Update reservation status
        reservation.setStatus("CANCELLED");
        Reservation.update(reservation);
        
        // Mark the spot as available
        parkingSpotController.markSpotAsAvailable(reservation.getSpotId());
        
        return true;
    }
    
    public List<Reservation> getUserReservations(Long userId) {
        List<Reservation> allReservations = Reservation.readAll();
        return allReservations.stream()
            .filter(reservation -> reservation.getUserId().equals(userId))
            .collect(Collectors.toList());
    }
    
    public List<Reservation> getSpotReservations(Long spotId) {
        List<Reservation> allReservations = Reservation.readAll();
        return allReservations.stream()
            .filter(reservation -> reservation.getSpotId().equals(spotId))
            .collect(Collectors.toList());
    }
    
    public Reservation getActiveReservation(Long spotId) {
        List<Reservation> spotReservations = getSpotReservations(spotId);
        return spotReservations.stream()
            .filter(reservation -> "ACTIVE".equals(reservation.getStatus()))
            .findFirst()
            .orElse(null);
    }
    
    public List<Reservation> getAllReservations() {
        return Reservation.readAll();
    }
    
    public List<Reservation> getActiveReservations() {
        return Reservation.readAll().stream()
            .filter(reservation -> "ACTIVE".equals(reservation.getStatus()))
            .collect(Collectors.toList());
    }
    
    public Reservation getReservation(Long reservationId) {
        return Reservation.read(reservationId);
    }
    
    public Reservation getLastReservation() {
        List<Reservation> allReservations = Reservation.readAll();
        return allReservations.isEmpty() ? null : allReservations.get(allReservations.size() - 1);
    }
    
    public boolean extendReservation(Long reservationId, LocalDateTime newEndTime) {
        Reservation reservation = Reservation.read(reservationId);
        if (reservation == null || !"ACTIVE".equals(reservation.getStatus())) {
            return false;
        }
        
        // Check if spot is available for the extended time
        if (!parkingSpotController.isSpotAvailable(reservation.getSpotId(), newEndTime)) {
            return false;
        }
        
        // Update reservation end time
        reservation.setEndTime(newEndTime);
        Reservation.update(reservation);
        
        return true;
    }
    
    public void checkAndUpdateExpiredReservations() {
        List<Reservation> activeReservations = getActiveReservations();
        LocalDateTime now = LocalDateTime.now();
        
        System.out.println("Checking " + activeReservations.size() + " active reservations for expiration");
        
        for (Reservation reservation : activeReservations) {
            if (reservation.getEndTime().isBefore(now)) {
                System.out.println("Found expired reservation: ID=" + reservation.getId() + 
                                 ", EndTime=" + reservation.getEndTime() + 
                                 ", CurrentTime=" + now);
                
                // Update reservation status to COMPLETED
                reservation.setStatus("COMPLETED");
                Reservation updatedReservation = Reservation.update(reservation);
                
                if (updatedReservation != null) {
                    // Mark the spot as available
                    parkingSpotController.markSpotAsAvailable(reservation.getSpotId());
                    
                    System.out.println("Successfully updated reservation " + reservation.getId() + 
                                     " from ACTIVE to COMPLETED");
                } else {
                    System.out.println("Failed to update reservation " + reservation.getId() + 
                                     " status to COMPLETED");
                }
            }
        }
    }
} 