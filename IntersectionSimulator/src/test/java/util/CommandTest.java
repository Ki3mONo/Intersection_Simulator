package util;

import environment.Road;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CommandTest {

    @Test
    void testSetType() {
        Command cmd = new Command();
        cmd.setType("addVehicle");
        assertEquals("addVehicle", cmd.getType());
    }

    @Test
    void testSetVehicleParams() {
        Command cmd = new Command();
        cmd.setType("addVehicle");
        cmd.setVehicleId("v1");
        cmd.setStartRoad("north");
        cmd.setEndRoad("south");

        assertEquals("v1", cmd.getVehicleId());
        assertEquals(Road.NORTH, cmd.getStartRoad());
        assertEquals(Road.SOUTH, cmd.getEndRoad());
    }

    @Test
    void testSetPedestrianParams() {
        Command cmd = new Command();
        cmd.setType("addPedestrian");
        cmd.setPedestrianId("p1");
        cmd.setStartRoad("east");
        cmd.setEndRoad("west");

        assertEquals("p1", cmd.getPedestrianId());
        assertEquals(Road.EAST, cmd.getStartRoad());
        assertEquals(Road.WEST, cmd.getEndRoad());
    }
}
