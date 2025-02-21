package environment;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RoadTest {

    @Test
    void testRoadEnumValues() {
        Road[] roads = Road.values();
        assertEquals(4, roads.length);

        assertTrue(contains(Road.NORTH, roads));
        assertTrue(contains(Road.SOUTH, roads));
        assertTrue(contains(Road.EAST, roads));
        assertTrue(contains(Road.WEST, roads));
    }

    private boolean contains(Road road, Road[] array) {
        for (Road r : array) {
            if (r == road) return true;
        }
        return false;
    }
}
