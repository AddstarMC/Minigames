package au.com.mineauz.minigames.tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ToolModes {

    private static Map<String, ToolMode> modes = new HashMap<>();

    static {
        addToolMode(new StartPositionMode());
        addToolMode(new SpectatorPositionMode());
        addToolMode(new QuitPositionMode());
        addToolMode(new EndPositionMode());
        addToolMode(new LobbyPositionMode());
        addToolMode(new RegenAreaMode());
        addToolMode(new DegenAreaMode());
    }

    public static void addToolMode(ToolMode mode) {
        if (modes.containsKey(mode.getName().toUpperCase()))
            throw new InvalidToolModeException("A tool mode already exists by this name!");
        else
            modes.put(mode.getName().toUpperCase(), mode);
    }

    public static List<ToolMode> getToolModes() {
        return new ArrayList<>(modes.values());
    }

    public static void removeToolMode(String name) {
        modes.remove(name.toUpperCase());
    }

    public static ToolMode getToolMode(String name) {
        if (modes.containsKey(name.toUpperCase()))
            return modes.get(name.toUpperCase());
        return null;
    }

}
