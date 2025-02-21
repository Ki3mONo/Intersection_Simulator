package environment;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RoadConfigTest {

    @Test
    void testRoadConfigCreation() {
        RoadConfig config = new RoadConfig(Road.NORTH, Axis.NS, 2, 5);

        assertEquals(Road.NORTH, config.getRoad());
        assertEquals(Axis.NS, config.getAxis());
        assertEquals(2, config.getLanes());
        assertEquals(5, config.getPriority());
    }
}
