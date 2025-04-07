package com.smartparking.repository;

import com.smartparking.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUserUserId(String userId);
    Reservation findByReservationId(String reservationId);
}