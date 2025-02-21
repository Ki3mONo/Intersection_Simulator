package simulation.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimulationResultTest {

    @Test
    void testSimulationResultCreation() {
        StepStatus status1 = new StepStatus();
        status1.addLeftVehicle("v1");
        status1.addLeftPedestrian("p1");

        StepStatus status2 = new StepStatus();
        status2.addLeftVehicle("v2");

        List<StepStatus> stepStatuses = new ArrayList<>();
        stepStatuses.add(status1);
        stepStatuses.add(status2);

        SimulationResult result = new SimulationResult(stepStatuses);
        assertEquals(2, result.getStepStatuses().size());
    }
}
