package entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ManeuverTest {

    @Test
    void testEnumValues() {
        Maneuver[] values = Maneuver.values();
        assertEquals(3, values.length);

        assertTrue(containsValue(Maneuver.STRAIGHT, values));
        assertTrue(containsValue(Maneuver.LEFT_TURN, values));
        assertTrue(containsValue(Maneuver.RIGHT_TURN, values));
    }

    private boolean containsValue(Maneuver value, Maneuver[] values) {
        for (Maneuver m : values) {
            if (m == value) return true;
        }
        return false;
    }
}
