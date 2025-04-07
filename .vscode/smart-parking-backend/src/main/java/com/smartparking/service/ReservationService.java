package com.smartparking.service;

import com.smartparking.model.Reservation;
import com.smartparking.model.ParkingSpot;
import com.smartparking.model.User;
import com.smartparking.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ParkingService parkingService;

    public ReservationService(ReservationRepository reservationRepository, ParkingService parkingService) {
        this.reservationRepository = reservationRepository;
        this.parkingService = parkingService;
    }

    public Reservation createReservation(User user, ParkingSpot spot, LocalDateTime startTime, LocalDateTime endTime) {
        Reservation reservation = new Reservation(user, spot, startTime, endTime);
        parkingService.updateSpotAvailability(spot.getSpotId(), false);
        return reservationRepository.save(reservation);
    }

    public void cancelReservation(String reservationId) {
        Reservation reservation = reservationRepository.findByReservationId(reservationId);
        if (reservation != null) {
            reservation.setStatus("CANCELLED");
            parkingService.updateSpotAvailability(reservation.getParkingSpot().getSpotId(), true);
            reservationRepository.save(reservation);
        }
    }

    public List<Reservation> getUserReservations(String userId) {
        return reservationRepository.findByUserUserId(userId);
    }

    public Reservation getReservationById(String reservationId) {
        return reservationRepository.findByReservationId(reservationId);
    }
}