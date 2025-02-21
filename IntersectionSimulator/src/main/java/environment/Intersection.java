package environment;

import entity.TrafficEntity;
import entity.Vehicle;
import entity.Maneuver;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Intersection - skrzyżowanie z dynamiczną sygnalizacją,
 * obsługujące fazy GREEN i YELLOW oraz cykl według osi (NS i EW).
 * W fazie GREEN, dla aktywnej osi:
 *   - Jeśli na dowolnej drodze znajdują się piesi, przetwarzani są oni
 *     oraz pojazdy jadące prosto, jeżeli znajdują się na początku kolejki.
 *   - Jeśli brak pieszych, pojazdy są przenoszone z kolejki do yellowBuffer,
 *     ale yellowBuffer dla każdej drogi przyjmuje maksymalnie tyle pojazdów,
 *     ile wynosi liczba pasów. Przy przenoszeniu, pojazdy skręcające w lewo ustępują
 *     pojazdom jadącym prosto lub skręcającym w prawo.
 * W fazie YELLOW, pojazdy zgromadzone w yellowBuffer są ostatecznie przepuszczane.
 * Po zakończeniu fazy YELLOW następuje zmiana aktywnej osi (NS / EW).
 */
public class Intersection {

    // Kolejki dla poszczególnych dróg.
    private final Map<Road, LinkedList<TrafficEntity>> entityQueues = new HashMap<>();
    // Bufory yellow – przechowują pojazdy, które już wjechały na skrzyżowanie.
    private final Map<Road, List<TrafficEntity>> yellowBuffers = new HashMap<>();
    // Konfiguracja dróg przekazywana przez kod kliencki.
    public final Map<Road, RoadConfig> roadConfigs;

    // Cykl oparty na osiach jezdni
    private final List<Axis> axisCycle = Arrays.asList(Axis.NS, Axis.EW);
    private int currentAxisIndex = 0; // Aktywna oś.

    // Definicja faz sygnalizacji.
    public enum Phase { GREEN, YELLOW }
    private Phase currentPhase;

    // Pozostały czas trwania bieżącej fazy.
    private int remainingTime;
    // Bazowy czas zielonego światła oraz stały czas żółtej fazy.
    private final int greenDuration;
    private final int yellowDuration;

    // Zestaw identyfikatorów pojazdów, które skręcają w lewo i ustąpiły
    private final Set<String> yieldedLeftTurn = new HashSet<>();

    // Konstruktor skrzyżowania z dynamiczną sygnalizacją. Parametr: mapa konfiguracji dróg, czas zielonego światła oraz czas żółtej fazy.
    public Intersection(Map<Road, RoadConfig> roadConfigs, int greenDuration, int yellowDuration) {
        this.roadConfigs = roadConfigs;
        this.greenDuration = greenDuration;
        this.yellowDuration = yellowDuration;

        // Inicjalizacja kolejek i buforów dla każdej drogi.
        for (Road r : Road.values()) {
            entityQueues.put(r, new LinkedList<>());
            yellowBuffers.put(r, new ArrayList<>());
        }
        // Rozpoczynamy od pierwszej osi w cyklu.
        currentPhase = Phase.GREEN;
        currentAxisIndex = 0;
        Axis activeAxis = axisCycle.get(currentAxisIndex);
        remainingTime = greenDuration * effectivePriority(activeAxis);
    }

    // Wyznacza efektywny priorytet dla danej osi (maksymalny priorytet spośród dróg należących do tej osi).
    private int effectivePriority(Axis axis) {
        int max = 0;
        for (Road r : Road.values()) {
            RoadConfig config = roadConfigs.get(r);
            if (config.getAxis() == axis && config.getPriority() > max) {
                max = config.getPriority();
            }
        }
        return max;
    }

    // Zwraca listę dróg należących do aktywnej osi.
    private List<Road> activeRoads() {
        Axis activeAxis = axisCycle.get(currentAxisIndex);
        List<Road> active = new ArrayList<>();
        for (Road r : Road.values()) {
            if (roadConfigs.get(r).getAxis() == activeAxis) {
                active.add(r);
            }
        }
        return active;
    }

    public void addEntity(Road road, TrafficEntity entity) {
        LinkedList<TrafficEntity> queue = entityQueues.get(road);
        if (queue == null) {
            System.out.println("Brak kolejki dla drogi: " + road);
        } else {
            queue.add(entity);
        }
    }

    // Wynik ruchu w jednym ticku.
    public static class MovementResult {
        private final List<String> passedEntities = new ArrayList<>();
        public List<String> getPassedEntities() { return passedEntities; }
    }

    /**
     * Symulacja jednego ticku.
     * Dla aktywnej osi (NS lub EW):
     * - W fazie GREEN:
     *     - Jeśli na danej drodze występują piesi, przetwarzamy ich wyłącznie,
     *       ale dodatkowo, jeśli na początku kolejki znajduje się pojazd jadący prosto,
     *       również go przepuszczamy (ponieważ nie koliduje z pieszymi).
     *     - Jeśli brak pieszych, pojazdy są przenoszone z kolejki do yellowBuffer,
     *       ale yellowBuffer dla każdej drogi przyjmuje maksymalnie tyle pojazdów, ile wynosi liczba pasów.
     *       Przy przenoszeniu, pojazdy skręcające w lewo ustępują pojazdom jadącym prosto lub skręcającym w prawo.
     * - W fazie YELLOW:
     *     - Wszystkie pojazdy zgromadzone w yellowBuffer dla aktywnych dróg są przepuszczane.
     * Po zakończeniu bieżącej fazy następuje przełączenie.
     */
    public MovementResult step() {
        MovementResult result = new MovementResult();
        List<Road> roads = activeRoads();
        if (currentPhase == Phase.GREEN) {
            for (Road r : roads) {
                LinkedList<TrafficEntity> queue = entityQueues.get(r);
                List<TrafficEntity> yellowBuffer = yellowBuffers.get(r);
                List<TrafficEntity> pedestrians = queue.stream()
                        .filter(TrafficEntity::isPedestrian)
                        .collect(Collectors.toList());
                if (!pedestrians.isEmpty()) {
                    for (TrafficEntity pedestrian : pedestrians) {
                        queue.remove(pedestrian);
                        result.getPassedEntities().add(pedestrian.getId());
                    }
                    while (!queue.isEmpty() && !queue.peek().isPedestrian() && isStraight(queue.peek())) {
                        TrafficEntity v = queue.poll();
                        result.getPassedEntities().add(v.getId());
                    }
                } else {
                    int lanes = roadConfigs.get(r).getLanes();
                    while (yellowBuffer.size() < lanes && !queue.isEmpty()) {
                        TrafficEntity front = queue.peek();
                        if (front == null) {
                            queue.poll();
                            continue;
                        }
                        if (!front.isPedestrian()) {
                            if (isLeftTurn(front)) {
                                boolean yield = false;
                                Iterator<TrafficEntity> it = queue.iterator();
                                if (it.hasNext()) it.next();
                                while (it.hasNext()) {
                                    TrafficEntity candidate = it.next();
                                    if (!candidate.isPedestrian() && !isLeftTurn(candidate)) {
                                        yield = true;
                                        break;
                                    }
                                }
                                if (yield) {
                                    TrafficEntity yielded = queue.poll();
                                    queue.add(yielded);
                                    yieldedLeftTurn.add(yielded.getId());
                                } else {
                                    queue.poll();
                                    yellowBuffer.add(front);
                                    yieldedLeftTurn.remove(front.getId());
                                }
                            } else {
                                queue.poll();
                                yellowBuffer.add(front);
                            }
                        } else {
                            queue.poll();
                        }
                    }
                }
            }
            remainingTime--;
            if (remainingTime <= 0) {
                currentPhase = Phase.YELLOW;
                remainingTime = yellowDuration;
            }
        } else if (currentPhase == Phase.YELLOW) {
            for (Road r : activeRoads()) {
                List<TrafficEntity> yellowBuffer = yellowBuffers.get(r);
                for (TrafficEntity vehicle : yellowBuffer) {
                    result.getPassedEntities().add(vehicle.getId());
                }
                yellowBuffer.clear();
                LinkedList<TrafficEntity> queue = entityQueues.get(r);
                while (!queue.isEmpty() && yieldedLeftTurn.contains(queue.peek().getId())) {
                    TrafficEntity v = queue.poll();
                    yieldedLeftTurn.remove(v.getId());
                    result.getPassedEntities().add(v.getId());
                }
            }
            remainingTime--;
            if (remainingTime <= 0) {
                currentAxisIndex = (currentAxisIndex + 1) % axisCycle.size();
                currentPhase = Phase.GREEN;
                Axis newAxis = axisCycle.get(currentAxisIndex);
                remainingTime = greenDuration * effectivePriority(newAxis) - 1;
            }
        }
        return result;
    }



    private boolean isLeftTurn(TrafficEntity entity) {
        if (!(entity instanceof Vehicle)) return false;
        Vehicle v = (Vehicle) entity;
        return v.getManeuver() == Maneuver.LEFT_TURN;
    }

    private boolean isStraight(TrafficEntity entity) {
        if (!(entity instanceof Vehicle)) return false;
        Vehicle v = (Vehicle) entity;
        return v.getManeuver() == Maneuver.STRAIGHT;
    }

    public Map<Road, Integer> getQueueSizes() {
        Map<Road, Integer> sizes = new HashMap<>();
        for (Road r : Road.values()) {
            sizes.put(r, entityQueues.get(r).size());
        }
        return sizes;
    }
}
