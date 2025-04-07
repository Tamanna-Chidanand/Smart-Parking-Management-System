package com.smartparking.controller;

import com.smartparking.model.Reservation;
import com.smartparking.model.User;
import com.smartparking.model.ParkingSpot;
import com.smartparking.service.ReservationService;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public Reservation createReservation(@RequestParam String userId, 
                                       @RequestParam String spotId,
                                       @RequestParam String startTime,
                                       @RequestParam String endTime) {
        User user = new User();
        user.setUserId(userId);
        
        ParkingSpot spot = new ParkingSpot();
        spot.setSpotId(spotId);
        
        LocalDateTime start = LocalDateTime.parse(startTime);
        LocalDateTime end = LocalDateTime.parse(endTime);
        
        return reservationService.createReservation(user, spot, start, end);
    }

    @GetMapping("/user/{userId}")
    public List<Reservation> getUserReservations(@PathVariable String userId) {
        return reservationService.getUserReservations(userId);
    }

    @DeleteMapping("/{reservationId}")
    public void cancelReservation(@PathVariable String reservationId) {
        reservationService.cancelReservation(reservationId);
    }
}