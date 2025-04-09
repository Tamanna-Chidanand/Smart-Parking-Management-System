package controller;

import model.Vehicle;
import java.util.ArrayList;
import java.util.List;

public class VehicleController {
    private List<Vehicle> vehicleList = new ArrayList<>();

    public void registerVehicle(String plate, String model) {
        vehicleList.add(new Vehicle(plate, model));
    }

    public List<Vehicle> getAllVehicles() {
        return vehicleList;
    }
}
