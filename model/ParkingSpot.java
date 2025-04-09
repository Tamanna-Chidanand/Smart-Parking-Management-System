package model;

public class ParkingSpot {
    private String spotId;
    private boolean available;

    public ParkingSpot(String spotId, boolean available) {
        this.spotId = spotId;
        this.available = available;
    }

    public String getSpotId() {
        return spotId;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
