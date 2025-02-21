package entity;

import environment.Road;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class VehicleTest {

    @Test
    void testVehicleCreation() {
        Vehicle vehicle = new Vehicle("v1", Road.NORTH, Road.SOUTH, Maneuver.STRAIGHT);

        assertEquals("v1", vehicle.getId());
        assertEquals(Road.NORTH, vehicle.getStartRoad());
        assertEquals(Road.SOUTH, vehicle.getEndRoad());
        assertFalse(vehicle.isTurning());  // STRAIGHT => isTurning = false
    }

    @Test
    void testVehicleTurningLeft() {
        Vehicle vehicle = new Vehicle("v2", Road.NORTH, Road.EAST, Maneuver.LEFT_TURN);
        assertTrue(vehicle.isTurning());
    }

    @Test
    void testVehicleTurningRight() {
        Vehicle vehicle = new Vehicle("v3", Road.SOUTH, Road.WEST, Maneuver.RIGHT_TURN);
        assertTrue(vehicle.isTurning());
    }

    @Test
    void testToString() {
        Vehicle vehicle = new Vehicle("v4", Road.WEST, Road.NORTH, Maneuver.STRAIGHT);
        String vStr = vehicle.toString();

        assertTrue(vStr.contains("v4"));
        assertTrue(vStr.contains("WEST"));
        assertTrue(vStr.contains("NORTH"));
        assertTrue(vStr.contains("STRAIGHT"));
    }
}
