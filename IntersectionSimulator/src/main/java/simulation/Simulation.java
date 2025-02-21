package simulation;

import entity.*;
import environment.*;
import simulation.model.SimulationResult;
import simulation.model.StepStatus;
import util.Command;
import java.util.ArrayList;
import java.util.List;

/**
 * Klasa reprezentująca symulację.
 */
public class Simulation {

    private final Intersection intersection;
    private final List<StepStatus> stepStatuses;

    // Konstruktor przyjmujący skonfigurowane Intersection.
    public Simulation(Intersection intersection) {
        this.intersection = intersection;
        this.stepStatuses = new ArrayList<>();
    }

    public SimulationResult run(List<Command> commands) {
        for (Command cmd : commands) {
            switch (cmd.getType()) {
                case "addVehicle" -> handleAddVehicle(cmd);
                case "addPedestrian" -> handleAddPedestrian(cmd);
                case "step" -> handleStep();
                default -> System.err.println("Nieznana komenda: " + cmd.getType());
            }
        }
        return new SimulationResult(stepStatuses);
    }

    private void handleAddVehicle(Command cmd) {
        String vehicleId = cmd.getVehicleId();
        Road startRoad = cmd.getStartRoad();
        Road endRoad = cmd.getEndRoad();
        Maneuver maneuver = deduceManeuver(startRoad, endRoad);
        Vehicle v = new Vehicle(vehicleId, startRoad, endRoad, maneuver);
        intersection.addEntity(startRoad, v);
    }

    private void handleAddPedestrian(Command cmd) {
        String pedestrianId = cmd.getPedestrianId();
        Road startRoad = cmd.getStartRoad();
        Road endRoad = cmd.getEndRoad();
        Pedestrian p = new Pedestrian(pedestrianId, startRoad, endRoad);
        intersection.addEntity(startRoad, p);
    }

    private void handleStep() {
        Intersection.MovementResult movementResult = intersection.step();
        List<String> passedEntities = movementResult.getPassedEntities();
        // Jeśli już mamy jakieś zapisy (czyli była zmiana) lub ten krok nie jest pusty,
        // to dodajemy nowy rekord.
        if (!passedEntities.isEmpty() || !stepStatuses.isEmpty()) {
            StepStatus stepStatus = new StepStatus();
            for (String entityId : passedEntities) {
                if (entityId.startsWith("p")) {
                    stepStatus.addLeftPedestrian(entityId);
                } else {
                    stepStatus.addLeftVehicle(entityId);
                }
            }
            stepStatuses.add(stepStatus);
        }
    }

    private entity.Maneuver deduceManeuver(environment.Road start, environment.Road end) {
        if ((start == environment.Road.NORTH && end == environment.Road.SOUTH) ||
                (start == environment.Road.SOUTH && end == environment.Road.NORTH) ||
                (start == environment.Road.EAST && end == environment.Road.WEST) ||
                (start == environment.Road.WEST && end == environment.Road.EAST)) {
            return entity.Maneuver.STRAIGHT;
        }
        if ((start == environment.Road.NORTH && end == environment.Road.EAST) ||
                (start == environment.Road.EAST && end == environment.Road.SOUTH) ||
                (start == environment.Road.SOUTH && end == environment.Road.WEST) ||
                (start == environment.Road.WEST && end == environment.Road.NORTH)) {
            return entity.Maneuver.RIGHT_TURN;
        }
        return entity.Maneuver.LEFT_TURN;
    }
}
