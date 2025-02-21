package entity;

import environment.Road;

/**
 * Interfejs reprezentujący pojazd lub pieszego w ruchu.
 */
public interface TrafficEntity {

    String getId();

    Road getStartRoad();

    Road getEndRoad();

    // Czy jest pieszym, domyślnie false, dla pojazdów
    default boolean isPedestrian() {
        return false;
    }

    // Czy skręca, domyślnie false, dla pieszych
    default boolean isTurning() {
        return false;
    }
}
