package entity;

import environment.Road;

/**
 * Klasa reprezentująca pojazd na skrzyżowaniu.
 */
public class Vehicle implements TrafficEntity {
    private final String vehicleId;
    private final Road startRoad;
    private final Road endRoad;
    private final Maneuver maneuver;

    public Vehicle(String vehicleId, Road startRoad, Road endRoad, Maneuver maneuver) {
        this.vehicleId = vehicleId;
        this.startRoad = startRoad;
        this.endRoad = endRoad;
        this.maneuver = maneuver;
    }

    @Override
    public String getId() {
        return vehicleId;
    }

    @Override
    public Road getStartRoad() {
        return startRoad;
    }

    @Override
    public Road getEndRoad() {
        return endRoad;
    }

    public Maneuver getManeuver() {
        return maneuver;
    }

    @Override
    public boolean isTurning() {
        return maneuver == Maneuver.LEFT_TURN || maneuver == Maneuver.RIGHT_TURN;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "vehicleId='" + vehicleId + '\'' +
                ", startRoad='" + startRoad + '\'' +
                ", endRoad='" + endRoad + '\'' +
                ", maneuver=" + maneuver +
                '}';
    }
}
