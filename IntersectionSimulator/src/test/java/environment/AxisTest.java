package environment;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AxisTest {

    @Test
    void testAxisEnumValues() {
        Axis[] axes = Axis.values();
        assertEquals(2, axes.length);

        assertTrue(contains(Axis.NS, axes));
        assertTrue(contains(Axis.EW, axes));
    }

    private boolean contains(Axis axis, Axis[] array) {
        for (Axis a : array) {
            if (a == axis) return true;
        }
        return false;
    }
}
