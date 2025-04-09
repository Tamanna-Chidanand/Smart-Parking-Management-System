package controller;

import model.Reservation;
import model.User;
import model.ParkingSpot;

import java.util.ArrayList;
import java.util.List;

public class ReservationController {
    private List<Reservation> reservationList = new ArrayList<>();
    private Reservation lastReservation;

    // ✅ Create reservation only if time slot is available
    public boolean createReservation(User user, ParkingSpot spot, String timeSlot, int durationInHours) {
        if (!isSpotAvailableForTime(spot, timeSlot)) {
            return false; // Deny reservation if time slot clashes
        }

        Reservation reservation = new Reservation(user, spot, timeSlot, durationInHours);
        reservationList.add(reservation);
        lastReservation = reservation;

        // Note: We don’t mark it as fully unavailable anymore — only reserved for this time
        return true;
    }

    // ✅ Time-based spot availability check
    public boolean isSpotAvailableForTime(ParkingSpot spot, String requestedTimeSlot) {
        for (Reservation r : reservationList) {
            if (r.getSpot().equals(spot) &&
                r.getTimeSlot().trim().equalsIgnoreCase(requestedTimeSlot.trim())) {
                return false;
            }
        }
        return true;
    }

    // ✅ Return all reservations
    public List<Reservation> getAllReservations() {
        return reservationList;
    }

    // ✅ For payment module
    public Reservation getLastReservation() {
        return lastReservation;
    }

    // ✅ Get all time slots for a specific spot
    public List<String> getReservedTimeSlotsForSpot(ParkingSpot spot) {
        List<String> times = new ArrayList<>();
        for (Reservation r : reservationList) {
            if (r.getSpot().equals(spot)) {
                times.add(r.getTimeSlot());
            }
        }
        return times;
    }
}
