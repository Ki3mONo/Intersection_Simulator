package util;

import com.fasterxml.jackson.annotation.JsonProperty;
import environment.Road;

/**
 * Model pojedynczej komendy JSON:
 *   - "addVehicle" (z parametrami vehicleId, startRoad, endRoad)
 *   - "addPedestrian" (pedestrianId, startRoad, endRoad)
 *   - "step"
 */
public class Command {

    @JsonProperty("type")
    private String type;
    @JsonProperty("vehicleId")
    private String vehicleId;
    @JsonProperty("startRoad")
    private String startRoad;
    @JsonProperty("endRoad")
    private String endRoad;
    @JsonProperty("pedestrianId")
    private String pedestrianId;

    public Command() {
    }

    public String getType() {
        return type;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public Road getStartRoad() {
        return processRoadFromStr(startRoad);
    }
    public Road getEndRoad() {
        return processRoadFromStr(endRoad);
    }

    public String getPedestrianId() {
        return pedestrianId;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public void setStartRoad(String startRoad) {
        this.startRoad = startRoad;
    }

    public void setEndRoad(String endRoad) {
        this.endRoad = endRoad;
    }

    public void setPedestrianId(String pedestrianId) {
        this.pedestrianId = pedestrianId;
    }

    private Road processRoadFromStr(String road) {
        if (road == null) {
            return null;
        }
        return switch (road.toLowerCase()) {
            case "north" -> Road.NORTH;
            case "south" -> Road.SOUTH;
            case "east" -> Road.EAST;
            case "west" -> Road.WEST;
            default -> null;
        };
    }
}
