package model;

public class Reservation {
    private User user;
    private ParkingSpot spot;
    private String timeSlot;
    private int durationInHours;

    public Reservation(User user, ParkingSpot spot, String timeSlot,int durationInHours) {
        this.user = user;
        this.spot = spot;
        this.timeSlot = timeSlot;
        this.durationInHours = durationInHours;
    }

    public User getUser() {
        return user;
    }

    public ParkingSpot getSpot() {
        return spot;
    }

    public String getTimeSlot() {
        return timeSlot;
    }
    public int getDurationInHours() {
        return durationInHours;
    }
}
