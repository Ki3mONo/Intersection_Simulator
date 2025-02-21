package simulation;

import environment.Axis;
import environment.Intersection;
import environment.Road;
import environment.RoadConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import simulation.model.SimulationResult;
import simulation.model.StepStatus;
import util.Command;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class SimulationTest {

    private Simulation simulation;

    @BeforeEach
    void setUp() {
        Map<Road, RoadConfig> roadConfigs = new HashMap<>();
        roadConfigs.put(Road.NORTH, new RoadConfig(Road.NORTH, Axis.NS, 2, 2));
        roadConfigs.put(Road.SOUTH, new RoadConfig(Road.SOUTH, Axis.NS, 2, 2));
        roadConfigs.put(Road.EAST, new RoadConfig(Road.EAST, Axis.EW, 1, 1));
        roadConfigs.put(Road.WEST, new RoadConfig(Road.WEST, Axis.EW, 1, 1));
        Intersection intersection = new Intersection(roadConfigs, 3, 1);
        simulation = new Simulation(intersection);
    }

    @Test
    void testRunWithEmptyCommands() {
        List<Command> commands = new ArrayList<>();
        SimulationResult result = simulation.run(commands);

        assertNotNull(result);
        assertNotNull(result.getStepStatuses());
        assertTrue(result.getStepStatuses().isEmpty());
    }

    @Test
    void testRunWithAddVehicleAndStep() {
        Command addVehicleCmd = new Command();
        addVehicleCmd.setType("addVehicle");
        addVehicleCmd.setVehicleId("v1");
        addVehicleCmd.setStartRoad("north");
        addVehicleCmd.setEndRoad("south");

        List<Command> commands = new ArrayList<>();
        commands.add(addVehicleCmd);
        for (int i = 0; i < 7; i++) {
            Command stepCmd = new Command();
            stepCmd.setType("step");
            commands.add(stepCmd);
        }
        SimulationResult result = simulation.run(commands);

        StepStatus stepStatus = result.getStepStatuses().get(result.getStepStatuses().size() - 1);
        assertTrue(stepStatus.getLeftVehicles().contains("v1"));
    }

    @Test
    void testRunWithAddVehicleAndAddPedestrian() {
        Command vehicleCmd = new Command();
        vehicleCmd.setType("addVehicle");
        vehicleCmd.setVehicleId("v2");
        vehicleCmd.setStartRoad("east");
        vehicleCmd.setEndRoad("west");

        Command pedestrianCmd = new Command();
        pedestrianCmd.setType("addPedestrian");
        pedestrianCmd.setPedestrianId("p1");
        pedestrianCmd.setStartRoad("east");
        pedestrianCmd.setEndRoad("west");

        List<Command> commands = new ArrayList<>();
        commands.add(vehicleCmd);
        commands.add(pedestrianCmd);
        for (int i = 0; i < 8; i++) {
            Command stepCmd = new Command();
            stepCmd.setType("step");
            commands.add(stepCmd);
        }

        SimulationResult result = simulation.run(commands);
        List<StepStatus> statuses = result.getStepStatuses();
        boolean foundPedestrian = statuses.stream().anyMatch(s -> s.getLeftPedestrians().contains("p1"));
        boolean foundVehicle = statuses.stream().anyMatch(s -> s.getLeftVehicles().contains("v2"));
        assertTrue(foundPedestrian);
        assertTrue(foundVehicle);
    }

}
