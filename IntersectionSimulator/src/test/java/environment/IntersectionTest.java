package environment;

import entity.Maneuver;
import entity.Pedestrian;
import entity.Vehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class IntersectionTest {

    private Intersection intersection;

    @BeforeEach
    void setUp() {
        // Domyślna konfiguracja: wszystkie drogi mają 2 pasy, priorytet 1
        Map<Road, RoadConfig> roadConfigs = new HashMap<>();
        roadConfigs.put(Road.NORTH, new RoadConfig(Road.NORTH, Axis.NS, 2, 1));
        roadConfigs.put(Road.SOUTH, new RoadConfig(Road.SOUTH, Axis.NS, 2, 1));
        roadConfigs.put(Road.EAST, new RoadConfig(Road.EAST, Axis.EW, 2, 1));
        roadConfigs.put(Road.WEST, new RoadConfig(Road.WEST, Axis.EW, 2, 1));
        // greenDuration = 3 ticki, yellowDuration = 1 tick
        intersection = new Intersection(roadConfigs, 3, 1);
    }

    @Test
    void testAddEntityAndQueueSizes() {
        Vehicle v1 = new Vehicle("v1", Road.NORTH, Road.SOUTH, Maneuver.STRAIGHT);
        Pedestrian p1 = new Pedestrian("p1", Road.NORTH, Road.SOUTH);

        intersection.addEntity(Road.NORTH, v1);
        intersection.addEntity(Road.NORTH, p1);

        assertEquals(2, intersection.getQueueSizes().get(Road.NORTH));
    }

    @Test
    void testGreenPhaseProcessesPedestriansAndStraightVehicle() {
        // Dla osi NS (aktywnej początkowo), dodajemy do NORTH:
        // - Pieszego i następnie pojazd jadący prosto.
        Pedestrian p1 = new Pedestrian("p1", Road.NORTH, Road.SOUTH);
        Vehicle v1 = new Vehicle("v1", Road.NORTH, Road.SOUTH, Maneuver.STRAIGHT);

        intersection.addEntity(Road.NORTH, p1);
        intersection.addEntity(Road.NORTH, v1);

        // Tick 1 (GREEN): powinien przetworzyć pieszych oraz (dodatkowo) pojazdy jadące prosto na początku kolejki.
        Intersection.MovementResult result1 = intersection.step();
        List<String> passed1 = result1.getPassedEntities();
        // Oczekujemy, że p1 oraz v1 (jadący prosto) przejdą natychmiast, bo piesi nie blokują pojazdów prostych
        assertTrue(passed1.contains("p1"));
        assertTrue(passed1.contains("v1"));
        // Kolejka dla NORTH powinna być pusta
        assertEquals(0, intersection.getQueueSizes().get(Road.NORTH));
    }

    @Test
    void testLeftTurnYieldsToStraight() {
        // Używamy konfiguracji z 1 pasem, by precyzyjniej kontrolować bufor.
        Map<Road, RoadConfig> customConfig = new HashMap<>();
        customConfig.put(Road.NORTH, new RoadConfig(Road.NORTH, Axis.NS, 1, 1));
        customConfig.put(Road.SOUTH, new RoadConfig(Road.SOUTH, Axis.NS, 1, 1));
        customConfig.put(Road.EAST, new RoadConfig(Road.EAST, Axis.EW, 1, 1));
        customConfig.put(Road.WEST, new RoadConfig(Road.WEST, Axis.EW, 1, 1));
        intersection = new Intersection(customConfig, 3, 1);

        // Dodajemy do NORTH kolejno: pojazd skręcający w lewo, potem pojazd jadący prosto.
        Vehicle leftTurnVehicle = new Vehicle("v1", Road.NORTH, Road.EAST, Maneuver.LEFT_TURN);
        Vehicle straightVehicle = new Vehicle("v2", Road.NORTH, Road.SOUTH, Maneuver.STRAIGHT);
        intersection.addEntity(Road.NORTH, leftTurnVehicle);
        intersection.addEntity(Road.NORTH, straightVehicle);

        // Tick 1 (GREEN): przetwarzamy kolejkę.
        Intersection.MovementResult result1 = intersection.step();
        List<String> passed1 = result1.getPassedEntities();
        // Ponieważ pierwszy element (v1) jest left-turn i za nim jest v2 (straight),
        // v1 zostaje przesunięty na koniec kolejki, więc w tym ticku nie przechodzi.
        assertFalse(passed1.contains("v1"));
        // Żaden pojazd nie trafia do wyjścia, bo w GREEN wyniki są zwracane jedynie, gdy są przetwarzani piesi lub proste na początku.
        // Sprawdzamy, że kolejka dla NORTH zawiera przynajmniej jeden element (v1).
        assertEquals(1, intersection.getQueueSizes().get(Road.NORTH));

        // Aby wyprowadzić v1, musimy przejść przez fazę YELLOW.
        // Symulujemy dodatkowe ticki aż do fazy YELLOW dla osi NS.
        intersection.step(); // Tick 2 (GREEN)
        Intersection.MovementResult result3 = intersection.step(); // Tick 3 (GREEN, koniec fazy, przechodzi do YELLOW)
        Intersection.MovementResult result4 = intersection.step(); // Tick 4 (YELLOW dla NS)
        intersection.step(); // Tick 5 (GREEN dla EW)
        List<String> passed4 = result4.getPassedEntities();
        // Teraz oczekujemy, że v1 zostanie przepuszczony w fazie YELLOW.
        assertTrue(passed4.contains("v1"));
    }

    @Test
    void testAxisSwitching() {
        // Dodajemy pojazdy na drogi NS oraz EW.
        // Początkowo aktywna oś to NS.
        Vehicle nsVehicle = new Vehicle("v_ns", Road.NORTH, Road.SOUTH, Maneuver.STRAIGHT);
        Vehicle ewVehicle = new Vehicle("v_ew", Road.EAST, Road.WEST, Maneuver.STRAIGHT);
        intersection.addEntity(Road.NORTH, nsVehicle);
        intersection.addEntity(Road.EAST, ewVehicle);

        // Symulujemy ticki dla osi NS:
        intersection.step(); // Tick 1 (GREEN NS)
        intersection.step(); // Tick 2 (GREEN NS)
        intersection.step(); // Tick 3 (GREEN NS) -> koniec GREEN, przejdziemy do YELLOW
        Intersection.MovementResult nsYellow = intersection.step(); // Tick 4 (YELLOW NS)
        assertTrue(nsYellow.getPassedEntities().contains("v_ns"));

        // Teraz, po fazie YELLOW, następuje zmiana osi – aktywna oś to EW, ale w fazie GREEN.
        Intersection.MovementResult ewGreen = intersection.step(); // Tick 5 (GREEN EW)
        // W fazie GREEN, pojazd z drogi EW jest przenoszony do yellowBuffer, ale nie wypisany.
        assertFalse(ewGreen.getPassedEntities().contains("v_ew"));
        // Kolejny tick (GREEN EW) może być pusty, a dopiero następny tick w fazie YELLOW wypisze v_ew.
        intersection.step(); // Tick 6 (GREEN EW)
        Intersection.MovementResult ewYellow = intersection.step(); // Tick 7 (YELLOW EW)
        assertTrue(ewYellow.getPassedEntities().contains("v_ew"));
    }

    @Test
    void testYellowBufferCapacity() {
        // Dla drogi NORTH z 2 pasami.
        // Dodajemy 3 pojazdy prostych; oczekujemy, że tylko 2 trafią do yellowBuffer, a 1 pozostanie w kolejce.
        Vehicle v1 = new Vehicle("v1", Road.NORTH, Road.SOUTH, Maneuver.STRAIGHT);
        Vehicle v2 = new Vehicle("v2", Road.NORTH, Road.SOUTH, Maneuver.STRAIGHT);
        Vehicle v3 = new Vehicle("v3", Road.NORTH, Road.SOUTH, Maneuver.STRAIGHT);
        intersection.addEntity(Road.NORTH, v1);
        intersection.addEntity(Road.NORTH, v2);
        intersection.addEntity(Road.NORTH, v3);

        // Tick 1 (GREEN NS): przetworzymy kolejkę – do yellowBuffer trafia maksymalnie 2 pojazdy.
        Intersection.MovementResult result1 = intersection.step();
        // W GREEN nie wypisujemy, tylko buforujemy – sprawdzamy kolejkę.
        // Kolejka dla NORTH powinna mieć 1 pojazd pozostały (nieprzeniesiony).
        assertEquals(1, intersection.getQueueSizes().get(Road.NORTH));

        // Aby przepuścić pojazdy z bufora, symulujemy przejście do fazy YELLOW.
        intersection.step(); // Tick 2 (GREEN)
        intersection.step(); // Tick 3 (GREEN, koniec fazy)
        Intersection.MovementResult resultYellow = intersection.step(); // Tick 4 (YELLOW NS)

        long count = resultYellow.getPassedEntities().stream()
                .filter(id -> id.startsWith("v"))
                .count();
        assertEquals(2, count);
    }
}