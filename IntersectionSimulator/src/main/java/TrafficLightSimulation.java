import com.fasterxml.jackson.databind.ObjectMapper;
import simulation.Simulation;
import simulation.model.SimulationResult;
import util.CommandsWrapper;
import environment.Intersection;
import environment.Road;
import environment.Axis;
import environment.RoadConfig;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Klasa główna programu, która uruchamia symulację skrzyżowania.
 * Wymaga 2 argumentów:
 *   1) ścieżka do pliku input.json (z komendami)
 *   2) ścieżka do pliku output.json (gdzie zapiszemy wynik)
 *
 * Aktualna Konfiguracja:
 * - Drogi NS (NORTH, SOUTH): 2 pasy, priorytet wyższy (np. 2)
 * - Drogi EW (EAST, WEST): 1 pas, priorytet niższy (np. 1)
 */
public class TrafficLightSimulation {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Użycie: java TrafficLightSimulation <input.json> <output.json>");
            System.exit(1);
        }

        String inputFilePath = args[0];
        String outputFilePath = args[1];

        try {
            ObjectMapper mapper = new ObjectMapper();
            CommandsWrapper commandsWrapper = mapper.readValue(new File(inputFilePath), CommandsWrapper.class);

            // Przygotowanie konfiguracji dróg:
            // Drogi NS: 2 pasy, priorytet wyższy (2)
            // Drogi EW: 1 pas, priorytet niższy (1)
            Map<Road, RoadConfig> roadConfigs = new HashMap<>();
            roadConfigs.put(Road.NORTH, new RoadConfig(Road.NORTH, Axis.NS, 2, 2));
            roadConfigs.put(Road.SOUTH, new RoadConfig(Road.SOUTH, Axis.NS, 2, 2));
            roadConfigs.put(Road.EAST, new RoadConfig(Road.EAST, Axis.EW, 1, 1));
            roadConfigs.put(Road.WEST, new RoadConfig(Road.WEST, Axis.EW, 1, 1));

            // Tworzymy Intersection z przygotowaną konfiguracją.
            Intersection intersection = new Intersection(roadConfigs, 3, 1);

            // Przekazujemy skonfigurowane Intersection do symulacji.
            Simulation simulation = new Simulation(intersection);
            SimulationResult result = simulation.run(commandsWrapper.getCommands());

            // Zapisujemy wynik do pliku output.json.
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(outputFilePath), result);
            System.out.println("Wynik symulacji zapisano w: " + outputFilePath);

        } catch (IOException e) {
            System.err.println("Błąd: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
