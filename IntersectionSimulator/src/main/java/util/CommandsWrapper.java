package util;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Obiekt służący do wczytania pliku JSON w postaci z treści zadania:
 * tj.
 * {
 *   "commands": [
 *     {"type": "addVehicle", ...},
 *     {"type": "step"},
 *     ...
 *   ]
 * }
 */
public class CommandsWrapper {

    @JsonProperty("commands")
    private List<Command> commands;

    public CommandsWrapper() {
    }

    public List<Command> getCommands() {
        return commands;
    }
}
