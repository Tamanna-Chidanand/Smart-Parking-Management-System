package controller;

import model.ParkingSpot;
import java.util.ArrayList;
import java.util.List;

public class ParkingSpotController {
    private List<ParkingSpot> spotList = new ArrayList<>();

    public ParkingSpotController() {
        for (char row = 'A'; row <= 'C'; row++) {
            for (int col = 1; col <= 5; col++) {
                String id = row + String.valueOf(col);
                spotList.add(new ParkingSpot(id, true));
            }
        }
    }

    public List<ParkingSpot> getAvailableSpots() {
        List<ParkingSpot> available = new ArrayList<>();
        for (ParkingSpot spot : spotList) {
            if (spot.isAvailable()) {
                available.add(spot);
            }
        }
        return available;
    }

    public List<ParkingSpot> getAllSpots() {
        return spotList;
    }

    public ParkingSpot getSpotById(String id) {
        for (ParkingSpot spot : spotList) {
            if (spot.getSpotId().equalsIgnoreCase(id)) {
                return spot;
            }
        }
        return null;
    }
}
