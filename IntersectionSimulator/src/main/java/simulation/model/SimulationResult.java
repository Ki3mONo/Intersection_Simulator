package simulation.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Klasa reprezentująca wynik symulacji.
 */
public class SimulationResult {

    @JsonProperty("stepStatuses")
    private List<StepStatus> stepStatuses;

    // Konstruktor przyjmujący listę
    public SimulationResult(List<StepStatus> stepStatuses) {
        this.stepStatuses = stepStatuses;
    }

    public List<StepStatus> getStepStatuses() {
        return stepStatuses;
    }

}
