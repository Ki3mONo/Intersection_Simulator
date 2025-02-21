import org.junit.jupiter.api.Test;
import java.io.File;
import static org.junit.jupiter.api.Assertions.*;

class TrafficLightSimulationTest {

    @Test
    void testMainWithSampleFiles() {
        String inputPath = "src/test/resources/testInput.json";
        String outputPath = "src/test/resources/testOutput.json";

        assertDoesNotThrow(() -> {
            TrafficLightSimulation.main(new String[] {inputPath, outputPath});
        });

        File outFile = new File(outputPath);
        assertTrue(outFile.exists());
    }
}
