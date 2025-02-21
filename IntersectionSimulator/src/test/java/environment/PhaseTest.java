package environment;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PhaseTest {

    @Test
    void testPhaseEnumValues() {
        Phase[] phases = Phase.values();
        assertEquals(2, phases.length);

        assertTrue(contains(Phase.GREEN, phases));
        assertTrue(contains(Phase.YELLOW, phases));
    }

    private boolean contains(Phase phase, Phase[] array) {
        for (Phase p : array) {
            if (p == phase) return true;
        }
        return false;
    }
}
