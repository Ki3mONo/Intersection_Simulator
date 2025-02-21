package entity;

import environment.Road;

/**
 * Klasa reprezentująca przechodnia na skrzyżowaniu.
 */
public class Pedestrian implements TrafficEntity {

    private final String pedestrianId;
    private final Road startRoad;
    private final Road endRoad;

    public Pedestrian(String pedestrianId, Road startRoad, Road endRoad) {
        this.pedestrianId = pedestrianId;
        this.startRoad = startRoad;
        this.endRoad = endRoad;
    }

    @Override
    public String getId() {
        return pedestrianId;
    }

    @Override
    public Road getStartRoad() {
        return startRoad;
    }

    @Override
    public Road getEndRoad() {
        return endRoad;
    }

    @Override
    public boolean isPedestrian() {
        return true;
    }

    @Override
    public String toString() {
        return "Pedestrian{" +
                "pedestrianId='" + pedestrianId + '\'' +
                ", startRoad='" + startRoad + '\'' +
                ", endRoad='" + endRoad + '\'' +
                '}';
    }
}
