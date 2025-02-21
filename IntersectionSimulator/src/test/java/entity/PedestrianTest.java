package entity;

import environment.Road;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PedestrianTest {

    @Test
    void testPedestrianCreation() {
        Pedestrian pedestrian = new Pedestrian("p1", Road.NORTH, Road.SOUTH);

        assertEquals("p1", pedestrian.getId());
        assertEquals(Road.NORTH, pedestrian.getStartRoad());
        assertEquals(Road.SOUTH, pedestrian.getEndRoad());
    }

    @Test
    void testIsPedestrian() {
        Pedestrian pedestrian = new Pedestrian("p2", Road.EAST, Road.WEST);
        assertTrue(pedestrian.isPedestrian());
    }

    @Test
    void testIsTurning() {
        Pedestrian pedestrian = new Pedestrian("p3", Road.NORTH, Road.SOUTH);
        assertFalse(pedestrian.isTurning());
    }

    @Test
    void testToString() {
        Pedestrian pedestrian = new Pedestrian("p4", Road.NORTH, Road.SOUTH);
        String pStr = pedestrian.toString();

        assertTrue(pStr.contains("p4"));
        assertTrue(pStr.contains("NORTH"));
        assertTrue(pStr.contains("SOUTH"));
    }
}
