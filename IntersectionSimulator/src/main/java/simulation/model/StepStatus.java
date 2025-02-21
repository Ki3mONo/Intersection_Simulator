package simulation.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Reprezentuje wynik pojedynczego kroku symulacji:
 *   - które pojazdy opuściły skrzyżowanie
 *   - którzy piesi przeszli
 */
public class StepStatus {

    @JsonProperty("leftVehicles")
    private List<String> leftVehicles;

    @JsonProperty("leftPedestrians")
    private List<String> leftPedestrians;

    public StepStatus() {
        this.leftVehicles = new ArrayList<>();
        this.leftPedestrians = new ArrayList<>();
    }

    public void addLeftVehicle(String vehicleId) {
        leftVehicles.add(vehicleId);
    }

    public void addLeftPedestrian(String pedestrianId) {
        leftPedestrians.add(pedestrianId);
    }

    public List<String> getLeftVehicles() {
        return leftVehicles;
    }

    public List<String> getLeftPedestrians() {
        return leftPedestrians;
    }
}
