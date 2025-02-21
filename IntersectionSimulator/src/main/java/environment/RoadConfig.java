package environment;

/**
 * Klasa przechowująca parametry drogi:
 *  - road: który Road (NORTH, SOUTH, itp.)
 *  - axis: do której osi (NS/EW) należy
 *  - lanes: liczba pasów
 *  - priority: priorytet
 */
public class RoadConfig {

    private final Road road;
    private final Axis axis;
    private final int lanes;
    private final int priority;

    public RoadConfig(Road road, Axis axis, int lanes, int priority) {
        this.road = road;
        this.axis = axis;
        this.lanes = lanes;
        this.priority = priority;
    }

    public Road getRoad() {
        return road;
    }

    public Axis getAxis() {
        return axis;
    }

    public int getLanes() {
        return lanes;
    }

    public int getPriority() {
        return priority;
    }
}
