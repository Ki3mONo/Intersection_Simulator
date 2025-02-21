package simulation.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StepStatusTest {

    @Test
    void testEmptyCollectionsInitially() {
        StepStatus stepStatus = new StepStatus();
        assertNotNull(stepStatus.getLeftVehicles());
        assertNotNull(stepStatus.getLeftPedestrians());
        assertTrue(stepStatus.getLeftVehicles().isEmpty());
        assertTrue(stepStatus.getLeftPedestrians().isEmpty());
    }

    @Test
    void testAddLeftVehicleAndPedestrian() {
        StepStatus stepStatus = new StepStatus();

        stepStatus.addLeftVehicle("v1");
        stepStatus.addLeftPedestrian("p1");

        assertEquals(1, stepStatus.getLeftVehicles().size());
        assertEquals(1, stepStatus.getLeftPedestrians().size());

        assertTrue(stepStatus.getLeftVehicles().contains("v1"));
        assertTrue(stepStatus.getLeftPedestrians().contains("p1"));
    }
}
